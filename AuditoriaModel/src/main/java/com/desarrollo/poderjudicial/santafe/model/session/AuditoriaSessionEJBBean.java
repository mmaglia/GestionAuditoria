package com.desarrollo.poderjudicial.santafe.model.session;

import com.desarrollo.poderjudicial.santafe.model.entidades.TablaAuditable;

import java.math.BigDecimal;

import java.math.RoundingMode;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

@Stateless(name = "AuditoriaSessionEJBBean", mappedName = "GestionAuditoria-AuditoriaModel-AuditoriaSessionEJBBean")
public class AuditoriaSessionEJBBean implements AuditoriaSessionEJB, AuditoriaSessionEJBBeanLocal {
    @Resource
    SessionContext sessionContext;
    @PersistenceContext(unitName = "Auditoria")
    private EntityManager em;

    public AuditoriaSessionEJBBean() {
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Object queryByRange(String jpqlStmt, int firstResult, int maxResults) {
        Query query = em.createQuery(jpqlStmt);
        if (firstResult > 0) {
            query = query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query = query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public <T> T persistEntity(T entity) {
        em.persist(entity);
        return entity;
    }

    public <T> T mergeEntity(T entity) {
        return em.merge(entity);
    }

    /**
     * Obtiene todas las tablas (Auditadas y No Auditadas) que son posibles de auditar
     * @return List<TablaAuditable> tablasAuditables
     */
    @Override
    @SuppressWarnings({ "oracle.jdeveloper.java.unchecked-conversion",
                        "oracle.jdeveloper.java.unchecked-conversion-or-cast",
                        "oracle.jdeveloper.java.comment-html-is-unterminated"
        })
    public List<TablaAuditable> obtenerTablasAuditables() {

        List<TablaAuditable> retorno = new ArrayList<TablaAuditable>();
        try {
            // Obtengo las tablas y el flag de auditable o no
            List<Object[]> tablas =
                em.createNativeQuery("SELECT a.table_name, 1 FROM user_tables a WHERE exists (SELECT trigger_name FROM user_triggers WHERE trigger_name = 'AUD#' || a.table_name) UNION ALL SELECT a.table_name, 0 FROM user_tables a WHERE not exists (SELECT trigger_name FROM user_triggers WHERE trigger_name = 'AUD#' || a.table_name) ORDER BY 1")
                .getResultList();
            
            // Obtengo el esquema
            Connection connection = em.unwrap(Connection.class);
            String esquemaDB = connection.getSchema();
            
            // Obtengo el espacio ocupado total del esquema
            BigDecimal espacioEsquema =
                (BigDecimal) em.createNativeQuery("SELECT sum(bytes)/1024/1024 FROM dba_segments WHERE owner = '" +
                                                  esquemaDB + "'").getSingleResult();

            for (Object[] elemento : tablas) {
                TablaAuditable tablaAuditable = new TablaAuditable();
                tablaAuditable.setNombreTabla(elemento[0].toString());

                // Determino la auditabiliadad
                BigDecimal isAuditable = new BigDecimal(elemento[1].toString());
                tablaAuditable.setIsAuditable(isAuditable.intValue() == 1);
                if (tablaAuditable.getIsAuditable()) {

                    // Obtengo la cantidad de registros auditados para la tabla auditada
                    BigDecimal cantRegistrosAud =
                        (BigDecimal) em.createNativeQuery("SELECT COUNT(*) FROM auditoria WHERE NOMBRE_TABLA = '" +
                                                          tablaAuditable.getNombreTabla() + "'").getSingleResult();
                    tablaAuditable.setCantRegistrosAud(cantRegistrosAud.intValue());

                    // Obtengo la cantidad de registros de la tabla
                    BigDecimal cantRegistros =
                        (BigDecimal) em.createNativeQuery("SELECT COUNT(*) FROM " + tablaAuditable.getNombreTabla())
                        .getSingleResult();
                    tablaAuditable.setCantRegistros(cantRegistros.intValue());

                    // Obtengo la cantidad de espacio en MB que ocupa todos los registros auditados de la tablaAuditable en la tabla auditoria
                    BigDecimal espacioTabla =
                        (BigDecimal) em.createNativeQuery("SELECT ROUND(sum(NVL(vsize('FECHA'),0) + NVL(vsize('USUARIO_DB'),0) + NVL(vsize('NOMBRE_TABLA'),0) + NVL(vsize('NOMBRE_CAMPO'),0) + NVL(vsize('ANTERIOR'),0) + NVL(vsize('NUEVO'),0) + \n" +
                                                          "NVL(vsize('OPERACION'),0) + NVL(vsize('ID_ENTIDAD'),0) + NVL(vsize('USUARIO_APP'),0))/1024/1024,4) " +
                                                          "FROM AUDITORIA " + "WHERE NOMBRE_TABLA = '" +
                                                          tablaAuditable.getNombreTabla() + "'").getSingleResult();
                    if (espacioTabla != null) {
                        tablaAuditable.setEspacioOcupado(espacioTabla.doubleValue());
                        // Obtengo el % de impacto sobre el total de MB ocupados
                        BigDecimal porcentaje = espacioTabla.divide(espacioEsquema, 2, RoundingMode.HALF_UP);

                        tablaAuditable.setImpacto(porcentaje.doubleValue());
                        porcentaje = null;
                    } else {
                        tablaAuditable.setEspacioOcupado(0.0);
                        tablaAuditable.setImpacto(0.0);
                    }


                    cantRegistros = null;
                    espacioTabla = null;

                }
                retorno.add(tablaAuditable);
            }
            espacioEsquema = null;
        } catch (Exception ex) {
            System.out.println("---- Error en Clase AuditoriaSessionEJBBean: obtenerTablasAuditables ----");
            ex.printStackTrace();
        }

        return retorno;
    }

    /**
     * Agrega auditoria a la tabla pasada por paámetros
     * @param nombreTabla
     * @return List<TablaAuditable> tablasAuditables
     */
    @Override
    @SuppressWarnings("oracle.jdeveloper.java.comment-html-is-unterminated")
    public List<TablaAuditable> auditarNuevaTabla(String nombreTabla) {
        List<TablaAuditable> retorno = null;

        if (nombreTabla == null || nombreTabla.isEmpty()) {
            System.out.println("---- Error en Clase AuditoriaSessionEJBBean: auditarNuevaTabla: nombreTabla is null ----");
            return retorno;
        }

        try {
            // Invoco al procedimiento almacenado para comenzar a auditar la tabla pasada por parámetro
            StoredProcedureQuery storedProdedure = em.createStoredProcedureQuery("SP_AUDITARIA_ACTIVAR_TABLA");
            storedProdedure.registerStoredProcedureParameter("PTABLA", String.class, ParameterMode.IN);
            storedProdedure.setParameter("PTABLA", nombreTabla);
            storedProdedure.execute();

            // Recupero la lista de tablas auditables
            retorno = this.obtenerTablasAuditables();
        } catch (Exception ex) {
            System.out.println("---- Error en Clase AuditoriaSessionEJBBean: auditarNuevaTabla ----");
            ex.printStackTrace();
        }
        return retorno;
    }

    /**
     * Quita la auditoria de la tabla pasada por parametro. 
     * Si el campo borrarDatosHistoricos viene true tambien se borran los datos historicos en la auditoria sobre la tabla en cuestion
     * @param nombreTabla
     * @param borrarDatosHistoricos
     * @return List<TablaAuditable> tablasAuditables
     */
    @Override
    @SuppressWarnings("oracle.jdeveloper.java.comment-html-is-unterminated")
    public List<TablaAuditable> desactivarAuditoria(String nombreTabla, Boolean borrarDatosHistoricos) {
        List<TablaAuditable> retorno = null;

        if (nombreTabla == null || nombreTabla.isEmpty()) {
            System.out.println("---- Error en Clase AuditoriaSessionEJBBean: desactivarAuditoria: nombreTabla is null ----");
            return retorno;
        }

        try {
            // Invoco al procedimiento almacenado para comenzar a desactivar la auditoría sobre la tabla pasada por parámetro
            StoredProcedureQuery storedProdedure = em.createStoredProcedureQuery("SP_AUDITORIA_DESACTIVAR_TABLA");
            storedProdedure.registerStoredProcedureParameter("PTABLA", String.class, ParameterMode.IN);
            storedProdedure.registerStoredProcedureParameter("PDELDATA", Integer.class, ParameterMode.IN);
            storedProdedure.setParameter("PTABLA", nombreTabla);
            // Dependiendo de si se desea o no borrar los datos historicos de la auditoria mando el segundo parametro como 1 o 0
            storedProdedure.setParameter("PDELDATA", borrarDatosHistoricos.booleanValue() ? 1 : 0);
            storedProdedure.execute();

            // Recupero la lista de tablas auditables
            retorno = this.obtenerTablasAuditables();

        } catch (Exception ex) {
            System.out.println("---- Error en Clase AuditoriaSessionEJBBean: desactivarAuditoria ----");
            ex.printStackTrace();
        }

        return retorno;
    }
}
