package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Comprador;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class CompradorDao {

    public void save(Comprador comprador) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(comprador);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error guardando comprador", e);
        }
    }

    public void update(Comprador comprador) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(comprador);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error actualizando comprador", e);
        }
    }

    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Comprador comprador = session.find(Comprador.class, id);
            if (comprador != null) {
                comprador.setActivo("N");
                session.merge(comprador);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error eliminando comprador", e);
        }
    }

    public Optional<Comprador> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Comprador comprador = session.find(Comprador.class, id);
            return Optional.ofNullable(comprador);
        }
    }

    public List<Comprador> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Comprador WHERE activo = 'S' ORDER BY nombre", Comprador.class)
                    .getResultList();
        }
    }

    public List<Comprador> findByAgente(Long agenteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Comprador> query = session.createQuery(
                    "FROM Comprador c WHERE c.agente.id = :agenteId AND c.activo = 'S' ORDER BY c.nombre",
                    Comprador.class
            );
            query.setParameter("agenteId", agenteId);
            return query.getResultList();
        }
    }

    public Optional<Comprador> findByUsuarioId(Long usuarioId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Comprador> query = session.createQuery(
                    "FROM Comprador c WHERE c.usuario.id = :usuarioId AND c.activo = 'S'",
                    Comprador.class
            );
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList().stream().findFirst();
        }
    }

    public List<Comprador> findByNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Comprador> query = session.createQuery(
                    "FROM Comprador c WHERE LOWER(c.nombre) LIKE LOWER(:nombre) AND c.activo = 'S' ORDER BY c.nombre",
                    Comprador.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        }
    }
}
