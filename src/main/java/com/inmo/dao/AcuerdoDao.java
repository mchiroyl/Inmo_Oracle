package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Acuerdo;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class AcuerdoDao {

    public void save(Acuerdo acuerdo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(acuerdo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error guardando acuerdo", e);
        }
    }

    public void update(Acuerdo acuerdo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(acuerdo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error actualizando acuerdo", e);
        }
    }

    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Acuerdo acuerdo = session.find(Acuerdo.class, id);
            if (acuerdo != null) {
                acuerdo.setEstado("CANCELADO");
                session.merge(acuerdo);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error eliminando acuerdo", e);
        }
    }

    public Optional<Acuerdo> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Acuerdo acuerdo = session.find(Acuerdo.class, id);
            return Optional.ofNullable(acuerdo);
        }
    }

    public List<Acuerdo> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Acuerdo a ORDER BY a.fechaAcuerdo DESC",
                    Acuerdo.class
            ).getResultList();
        }
    }

    public List<Acuerdo> findByAgente(Long agenteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Acuerdo> query = session.createQuery(
                    "FROM Acuerdo a WHERE a.agente.id = :agenteId ORDER BY a.fechaAcuerdo DESC",
                    Acuerdo.class
            );
            query.setParameter("agenteId", agenteId);
            return query.getResultList();
        }
    }

    public List<Acuerdo> findByComprador(Long compradorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Acuerdo> query = session.createQuery(
                    "FROM Acuerdo a WHERE a.comprador.id = :compradorId ORDER BY a.fechaAcuerdo DESC",
                    Acuerdo.class
            );
            query.setParameter("compradorId", compradorId);
            return query.getResultList();
        }
    }

    public List<Acuerdo> findByVendedor(Long vendedorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Acuerdo> query = session.createQuery(
                    "FROM Acuerdo a WHERE a.vendedor.id = :vendedorId ORDER BY a.fechaAcuerdo DESC",
                    Acuerdo.class
            );
            query.setParameter("vendedorId", vendedorId);
            return query.getResultList();
        }
    }

    public List<Acuerdo> findByEstado(String estado) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Acuerdo> query = session.createQuery(
                    "FROM Acuerdo a WHERE a.estado = :estado ORDER BY a.fechaAcuerdo DESC",
                    Acuerdo.class
            );
            query.setParameter("estado", estado);
            return query.getResultList();
        }
    }

    public List<Acuerdo> findCompletados() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Acuerdo> query = session.createQuery(
                    "FROM Acuerdo a WHERE a.estado = 'COMPLETADO' ORDER BY a.fechaCierre DESC",
                    Acuerdo.class
            );
            return query.getResultList();
        }
    }
}
