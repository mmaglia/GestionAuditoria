package com.desarrollo.poderjudicial.santafe.model.session;

import com.desarrollo.poderjudicial.santafe.model.entidades.TablaAuditable;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface AuditoriaSessionEJB {
    Object queryByRange(String jpqlStmt, int firstResult, int maxResults);

    <T> T persistEntity(T entity);

    <T> T mergeEntity(T entity);
    
    List<TablaAuditable> obtenerTablasAuditables();
    
    List<TablaAuditable> auditarNuevaTabla(String nombreTabla);
    
    List<TablaAuditable> desactivarAuditoria(String nombreTabla, Boolean borrarDatosHistoricos);
}
