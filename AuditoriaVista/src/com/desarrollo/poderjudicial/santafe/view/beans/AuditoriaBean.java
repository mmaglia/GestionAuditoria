package com.desarrollo.poderjudicial.santafe.view.beans;

import com.desarrollo.poderjudicial.santafe.model.entidades.TablaAuditable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.OperationBinding;
import oracle.adf.view.rich.component.rich.data.RichListView;
import oracle.adf.view.rich.component.rich.layout.RichPanelList;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.binding.BindingContainer;

import oracle.jbo.Row;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.util.ComponentReference;

/**
 * Bean Manejador de la pantalla de Gestion de Auditoría
 * @author jalarcon
 */
public class AuditoriaBean {

    // Componentes visuales
    private ComponentReference listView;
    private ComponentReference panelList;

    // Atributos
    private List<TablaAuditable> auditables = null;
    private String totalRegistrosAuditados = null;
    private String totalTablas = null;
    private String totalTablasAuditados = null;
    private String totalImpacto = null;
    private boolean ordernarPorTabla = true;
    private boolean ordernarPorTotal = true;
    private boolean ordernarPorTotalAuditadas = true;
    private boolean ordernarPorAuditadas = true;
    private boolean ordernarPorEspacio = true;
    private boolean ordernarPorImpacto = true;

    private String filaNombreTabla = null;

    // Construnctor default
    public AuditoriaBean() {
        this.auditables = new ArrayList<TablaAuditable>();

    }

    // Getters and Setters

    public void setFilaNombreTabla(String filaNombreTabla) {
        this.filaNombreTabla = filaNombreTabla;
    }

    public String getFilaNombreTabla() {
        return filaNombreTabla;
    }

    public void setListView(RichListView listView) {
        this.listView = ComponentReference.newUIComponentReference(listView);
    }

    public RichListView getListView() {
        if (this.listView != null) {
            return (RichListView) this.listView.getComponent();
        }
        return null;
    }

    public void setPanelList(RichPanelList panelList) {
        this.panelList = ComponentReference.newUIComponentReference(panelList);
    }

    public RichPanelList getPanelList() {
        if (this.panelList != null) {
            return (RichPanelList) this.panelList.getComponent();
        }
        return null;
    }

    public void setAuditables(List<TablaAuditable> auditables) {
        this.auditables = auditables;
    }

    public List<TablaAuditable> getAuditables() {
        return auditables;
    }

    public void setTotalRegistrosAuditados(String totalRegistrosAuditados) {
        this.totalRegistrosAuditados = totalRegistrosAuditados;
    }

    public String getTotalRegistrosAuditados() {
        return totalRegistrosAuditados;
    }

    public void setTotalTablas(String totalTablas) {
        this.totalTablas = totalTablas;
    }

    public String getTotalTablas() {
        return totalTablas;
    }

    public void setTotalTablasAuditados(String totalTablasAuditados) {
        this.totalTablasAuditados = totalTablasAuditados;
    }

    public String getTotalTablasAuditados() {
        return totalTablasAuditados;
    }

    public void setTotalImpacto(String totalImpacto) {
        this.totalImpacto = totalImpacto;
    }

    public String getTotalImpacto() {
        return totalImpacto;
    }

    /**
     * Cuando carga la vista relleno el ListView
     * @param clientEvent
     */
    @SuppressWarnings("oracle.jdeveloper.java.unchecked-conversion-or-cast")
    public void inicializarListView(ClientEvent clientEvent) {

        // Obtengo las tablas auditables del servidor
        BindingContainer bindingContainer = BindingContext.getCurrent().getCurrentBindingsEntry();
        OperationBinding opb = (OperationBinding) bindingContainer.getOperationBinding("obtenerTablasAuditables");
        this.auditables = (List<TablaAuditable>) opb.execute();

        // Inicializo los valores y ordenadores
        this.inicializarValores();
    }

    /**
     * Inicializa valores y ordenadores
     */
    private void inicializarValores() {
        // Sumarizo los totales
        this.sumarizarTotales();

        // Pongo el orden descendente para todos los tipos
        this.ordernarPorTabla = false;
        this.ordernarPorAuditadas = false;
        this.ordernarPorTotal = false;
        this.ordernarPorTotalAuditadas = false;
        this.ordernarPorImpacto = false;
        this.ordernarPorEspacio = false;

        // Refesco el List View para que tome los cambios
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getListView());
    }

    /**
     * Sumariza los totales de las tablas auditables
     */
    private void sumarizarTotales() {
        Integer totalRegistrosAuditados = 0;
        Double totalEspacio = 0.0;
        Double totalImpacto = 0.0;
        Integer totalTablasAuditadas = 0;
        for (TablaAuditable elemento : this.getAuditables()) {
            if (elemento.getIsAuditable()) {
                totalRegistrosAuditados += elemento.getCantRegistrosAud();
                totalEspacio += elemento.getEspacioOcupado();
                totalImpacto += elemento.getImpacto();
                totalTablasAuditadas++;
            }
        }

        this.setTotalImpacto(totalImpacto.toString());
        this.setTotalRegistrosAuditados(totalRegistrosAuditados.toString());
        this.setTotalTablas(this.getAuditables().size() + "");
        this.setTotalTablasAuditados(totalTablasAuditadas.toString());
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getPanelList());
    }

    /**
     * Ordena las tablas auditables por nombre de tabla
     */
    @SuppressWarnings({ "oracle.jdeveloper.java.unchecked-conversion",
                        "oracle.jdeveloper.java.unchecked-conversion-or-cast" })
    public void ordenarPorTabla(ActionEvent actionEvent) {
        // Ordeno la lista de las auditables por el nombre de tabla
        Collections.sort(getAuditables(), Comparator.comparing(TablaAuditable::toString));
        if (!ordernarPorTabla) {
            // Ordeno descendente
            Collections.reverse(getAuditables());
        }

        // Cambio el orden
        this.ordernarPorTabla = !this.ordernarPorTabla;

        // Refesco el List View para que tome los cambios
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getListView());
    }

    /**
     * Ordena las tablas auditables por espacio ocupado
     */
    @SuppressWarnings({ "oracle.jdeveloper.java.unchecked-conversion",
                        "oracle.jdeveloper.java.unchecked-conversion-or-cast" })
    public void ordenarPorEspacio(ActionEvent actionEvent) {
        // Ordeno la lista de las auditables por el espacio que ocupa
        if (ordernarPorEspacio) {
            // Ordeno ascendentemente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getEspacioOcupado,
                                                  Comparator.nullsFirst(Double::compareTo)));
            Collections.reverse(getAuditables());
        } else {
            // Ordeno descendente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getEspacioOcupado,
                                                  Comparator.nullsLast(Double::compareTo)));
        }

        // Cambio el orden
        this.ordernarPorEspacio = !this.ordernarPorEspacio;

        // Refesco el List View para que tome los cambios
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getListView());
    }

    /**
     *  Ordena las tablas auditables por el impacto causado
     */
    @SuppressWarnings({ "oracle.jdeveloper.java.unchecked-conversion",
                        "oracle.jdeveloper.java.unchecked-conversion-or-cast" })
    public void ordenarPorImpacto(ActionEvent actionEvent) {
        // Ordeno la lista de las auditables por el impacto
        if (ordernarPorImpacto) {
            // Ordeno ascendentemente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getImpacto,
                                                  Comparator.nullsFirst(Double::compareTo)));
            Collections.reverse(getAuditables());
        } else {
            // Ordeno descendente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getImpacto, Comparator.nullsLast(Double::compareTo)));
        }

        // Cambio el orden
        this.ordernarPorImpacto = !this.ordernarPorImpacto;

        // Refesco el List View para que tome los cambios
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getListView());
    }

    /**
     * Ordena las tablas auditables por el total de registros de cada una
     */
    @SuppressWarnings({ "oracle.jdeveloper.java.unchecked-conversion",
                        "oracle.jdeveloper.java.unchecked-conversion-or-cast" })
    public void ordenarPorTotal(ActionEvent actionEvent) {
        // Ordeno la lista de las auditables por total de registros
        if (ordernarPorTotal) {
            // Ordeno ascendentemente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getCantRegistros,
                                                  Comparator.nullsFirst(Integer::compareTo)));
            Collections.reverse(getAuditables());
        } else {
            // Ordeno descendente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getCantRegistros,
                                                  Comparator.nullsLast(Integer::compareTo)));
        }

        // Cambio el orden
        this.ordernarPorTotal = !this.ordernarPorTotal;

        // Refesco el List View para que tome los cambios
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getListView());
    }

    /**
     * Ordena las tablas auditables si estan auditadas o no
     */
    @SuppressWarnings({ "oracle.jdeveloper.java.unchecked-conversion",
                        "oracle.jdeveloper.java.unchecked-conversion-or-cast" })
    public void ordenarPorAuditadas(ActionEvent actionEvent) {
        // Ordeno la lista de las auditables por si estan auditadas o no
        Collections.sort(getAuditables(), Comparator.comparing(TablaAuditable::getIsAuditable));
        if (!ordernarPorAuditadas) {
            // Ordeno descendente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getCantRegistros,
                                                  Comparator.nullsLast(Integer::compareTo)));
        }

        // Cambio el orden
        this.ordernarPorAuditadas = !this.ordernarPorAuditadas;

        // Refesco el List View para que tome los cambios
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getListView());
    }

    /**
     * Ordena las tablas auditables por el total de registros auditados de cada una
     */
    public void ordenarPorTotalAuditadas(ActionEvent actionEvent) {
        // Ordeno la lista de las auditables por total de registros auditados
        if (ordernarPorTotalAuditadas) {
            // Ordeno ascendentemente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getCantRegistrosAud,
                                                  Comparator.nullsFirst(Integer::compareTo)));
            Collections.reverse(getAuditables());
        } else {
            // Ordeno descendente
            Collections.sort(getAuditables(),
                             Comparator.comparing(TablaAuditable::getCantRegistrosAud,
                                                  Comparator.nullsLast(Integer::compareTo)));
        }

        // Cambio el orden
        this.ordernarPorTotalAuditadas = !this.ordernarPorTotalAuditadas;

        // Refesco el List View para que tome los cambios
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getListView());
    }

    /**
     * Listener que toma la accion de auditar tabla
     * @param actionEvent
     */
    @SuppressWarnings("oracle.jdeveloper.java.unchecked-conversion-or-cast")
    public void auditarNuevaTabla(ActionEvent actionEvent) {

        // Invoco al servidor para auditar nueva tabla
        BindingContainer bindingContainer = BindingContext.getCurrent().getCurrentBindingsEntry();
        OperationBinding opb = (OperationBinding) bindingContainer.getOperationBinding("auditarNuevaTabla");
        opb.getParamsMap().put("nombreTabla", this.filaNombreTabla);
        this.auditables = (List<TablaAuditable>) opb.execute();

        this.inicializarValores();
    }

    /**
     * Listener que toma las acciones del dialogo de borrar auditoria
     * @param dialogEvent
     */
    @SuppressWarnings("oracle.jdeveloper.java.unchecked-conversion-or-cast")
    public void listenerDialogoBorrarAuditoria(DialogEvent dialogEvent) {

        // Invoco al servidor para desactivar auditoria de tabla
        BindingContainer bindingContainer = BindingContext.getCurrent().getCurrentBindingsEntry();
        OperationBinding opb = (OperationBinding) bindingContainer.getOperationBinding("desactivarAuditoria");
        opb.getParamsMap().put("nombreTabla", this.filaNombreTabla);
        opb.getParamsMap().put("borrarDatosHistoricos", dialogEvent.getOutcome() == DialogEvent.Outcome.yes);
        this.auditables = (List<TablaAuditable>) opb.execute();
        
        this.inicializarValores();
    }
}
