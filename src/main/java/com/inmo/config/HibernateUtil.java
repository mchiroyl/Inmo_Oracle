package com.inmo.config;

import com.inmo.domain.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration().configure(); // hibernate.cfg.xml

            // Entidades registradas
            cfg.addAnnotatedClass(Usuario.class);
            cfg.addAnnotatedClass(Agente.class);
            cfg.addAnnotatedClass(Vendedor.class);
            cfg.addAnnotatedClass(Comprador.class);
            cfg.addAnnotatedClass(Inmueble.class);
            cfg.addAnnotatedClass(Oferta.class);
            cfg.addAnnotatedClass(Contraoferta.class);
            cfg.addAnnotatedClass(Acuerdo.class);

            return cfg.buildSessionFactory();
        } catch (Exception ex) {
            System.err.println("ERROR creando SessionFactory: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
