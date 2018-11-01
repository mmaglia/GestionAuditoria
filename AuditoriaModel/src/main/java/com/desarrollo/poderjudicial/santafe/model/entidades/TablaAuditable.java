package com.desarrollo.poderjudicial.santafe.model.entidades;

/**
 * Clase Java que representa una tabla auditable
 * @author jalarcon
 */
public class TablaAuditable {

    private String nombreTabla = null;
    private Boolean isAuditable = null;
    private Integer cantRegistros = null;
    private Integer cantRegistrosAud = null;
    private Double espacioOcupado = null;
    private Double impacto = null;

    /**
     * Constructos default
     */
    public TablaAuditable() {
    }

    /**
     * Constructor
     * @param nombreTabla
     * @param isAuditable
     * @param registrosAuditados
     * @param espacioOcupado
     * @param impacto
     */
    public TablaAuditable(String nombreTabla, Boolean isAuditable, Integer cantRegistros, Integer cantRegistrosAud, Double espacioOcupado,
                          Double impacto) {
        this.nombreTabla = nombreTabla;
        this.isAuditable = isAuditable;
        this.cantRegistros = cantRegistros;
        this.cantRegistrosAud = cantRegistrosAud;
        this.espacioOcupado = espacioOcupado;
        this.impacto = impacto;
    }

    public void setNombreTabla(String nombreTabla) {
        this.nombreTabla = nombreTabla;
    }

    public String getNombreTabla() {
        return nombreTabla;
    }

    public void setIsAuditable(Boolean isAuditable) {
        this.isAuditable = isAuditable;
    }

    public Boolean getIsAuditable() {
        return isAuditable;
    }

    public void setCantRegistros(Integer cantRegistros) {
        this.cantRegistros = cantRegistros;
    }

    public Integer getCantRegistros() {
        return cantRegistros;
    }

    public void setCantRegistrosAud(Integer cantRegistrosAud) {
        this.cantRegistrosAud = cantRegistrosAud;
    }

    public Integer getCantRegistrosAud() {
        return cantRegistrosAud;
    }


    public void setEspacioOcupado(Double espacioOcupado) {
        this.espacioOcupado = espacioOcupado;
    }

    public Double getEspacioOcupado() {
        return espacioOcupado;
    }

    public void setImpacto(Double impacto) {
        this.impacto = impacto;
    }

    public Double getImpacto() {
        return impacto;
    }

    public String toString() {
        return this.getNombreTabla();
    }
}
