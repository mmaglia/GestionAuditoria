package com.desarrollo.poderjudicial.santafe.model.entidades;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = "Tipodoc.findAll", query = "select o from Tipodoc o") })
public class Tipodoc implements Serializable {
    private static final long serialVersionUID = -1810185511180390605L;
    @Column(length = 20)
    private String desctipodoc;
    @Id
    @Column(nullable = false)
    private Long idtipodoc;
    @Column(name = "USUARIO_APP", length = 50)
    private String usuarioApp;

    public Tipodoc() {
    }

    public Tipodoc(String desctipodoc, Long idtipodoc, String usuarioApp) {
        this.desctipodoc = desctipodoc;
        this.idtipodoc = idtipodoc;
        this.usuarioApp = usuarioApp;
    }

    public String getDesctipodoc() {
        return desctipodoc;
    }

    public void setDesctipodoc(String desctipodoc) {
        this.desctipodoc = desctipodoc;
    }

    public Long getIdtipodoc() {
        return idtipodoc;
    }

    public void setIdtipodoc(Long idtipodoc) {
        this.idtipodoc = idtipodoc;
    }

    public String getUsuarioApp() {
        return usuarioApp;
    }

    public void setUsuarioApp(String usuarioApp) {
        this.usuarioApp = usuarioApp;
    }
}
