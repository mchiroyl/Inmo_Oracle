package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Oferta;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class OfertaDao {

    public void save(Oferta oferta) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(oferta);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error guardando oferta", e);
        }
    }

    public void update(Oferta oferta) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(oferta);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error actualizando oferta", e);
        }
    }

    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Oferta oferta = session.find(Oferta.class, id);
            if (oferta != null) {
                oferta.setEstado("CANCELADA");
                session.merge(oferta);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error eliminando oferta", e);
        }
    }

    public Optional<Oferta> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Oferta oferta = session.find(Oferta.class, id);
            return Optional.ofNullable(oferta);
        }
    }

    public List<Oferta> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Oferta o ORDER BY o.prioridad ASC, o.id DESC",
                    Oferta.class
            ).getResultList();
        }
    }

    public List<Oferta> findByInmueble(Long inmuebleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Oferta> query = session.createQuery(
                    "FROM Oferta o WHERE o.inmueble.id = :inmuebleId ORDER BY o.prioridad ASC, o.id DESC",
                    Oferta.class
            );
            query.setParameter("inmuebleId", inmuebleId);
            return query.getResultList();
        }
    }

    public List<Oferta> findByComprador(Long compradorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Oferta> query = session.createQuery(
                    "FROM Oferta o WHERE o.comprador.id = :compradorId ORDER BY o.id DESC",
                    Oferta.class
            );
            query.setParameter("compradorId", compradorId);
            return query.getResultList();
        }
    }

    public List<Oferta> findByEstado(String estado) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Oferta> query = session.createQuery(
                    "FROM Oferta o WHERE o.estado = :estado ORDER BY o.prioridad ASC, o.id DESC",
                    Oferta.class
            );
            query.setParameter("estado", estado);
            return query.getResultList();
        }
    }

    public List<Oferta> findPendientesByVendedor(Long vendedorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Oferta> query = session.createQuery(
                    "FROM Oferta o WHERE o.inmueble.vendedor.id = :vendedorId " +
                    "AND o.estado = 'PENDIENTE' ORDER BY o.prioridad ASC, o.id DESC",
                    Oferta.class
            );
            query.setParameter("vendedorId", vendedorId);
            return query.getResultList();
        }
    }

    public List<Oferta> findByAgente(Long agenteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Oferta> query = session.createQuery(
                    "FROM Oferta o WHERE o.agente.id = :agenteId ORDER BY o.id DESC",
                    Oferta.class
            );
            query.setParameter("agenteId", agenteId);
            return query.getResultList();
        }
    }
}
