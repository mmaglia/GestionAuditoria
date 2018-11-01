package com.desarrollo.poderjudicial.santafe.model.test;

import com.desarrollo.poderjudicial.santafe.model.entidades.TablaAuditable;

public class JavaServiceFacadeClient {
    public static void main(String[] args) {
        try {
            final JavaServiceFacade javaServiceFacade = new JavaServiceFacade();
            
            javaServiceFacade.desactivarAuditoria("asigpat", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
