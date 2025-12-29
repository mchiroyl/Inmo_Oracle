package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Contraoferta;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ContraofertaDao {

    public void save(Contraoferta contraoferta) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(contraoferta);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error guardando contraoferta", e);
        }
    }

    public void update(Contraoferta contraoferta) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(contraoferta);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error actualizando contraoferta", e);
        }
    }

    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Contraoferta contraoferta = session.find(Contraoferta.class, id);
            if (contraoferta != null) {
                contraoferta.setEstado("CANCELADA");
                session.merge(contraoferta);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error eliminando contraoferta", e);
        }
    }

    public Optional<Contraoferta> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Contraoferta contraoferta = session.find(Contraoferta.class, id);
            return Optional.ofNullable(contraoferta);
        }
    }

    public List<Contraoferta> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Contraoferta c ORDER BY c.fechaContraoferta DESC",
                    Contraoferta.class
            ).getResultList();
        }
    }

    public List<Contraoferta> findByOferta(Long ofertaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Contraoferta> query = session.createQuery(
                    "FROM Contraoferta c WHERE c.oferta.id = :ofertaId ORDER BY c.fechaContraoferta DESC",
                    Contraoferta.class
            );
            query.setParameter("ofertaId", ofertaId);
            return query.getResultList();
        }
    }

    public List<Contraoferta> findByVendedor(Long vendedorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Contraoferta> query = session.createQuery(
                    "FROM Contraoferta c WHERE c.vendedor.id = :vendedorId ORDER BY c.fechaContraoferta DESC",
                    Contraoferta.class
            );
            query.setParameter("vendedorId", vendedorId);
            return query.getResultList();
        }
    }

    public List<Contraoferta> findByEstado(String estado) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Contraoferta> query = session.createQuery(
                    "FROM Contraoferta c WHERE c.estado = :estado ORDER BY c.fechaContraoferta DESC",
                    Contraoferta.class
            );
            query.setParameter("estado", estado);
            return query.getResultList();
        }
    }

    public List<Contraoferta> findByComprador(Long compradorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Contraoferta> query = session.createQuery(
                    "FROM Contraoferta c WHERE c.oferta.comprador.id = :compradorId " +
                    "AND c.estado = 'PENDIENTE' ORDER BY c.fechaContraoferta DESC",
                    Contraoferta.class
            );
            query.setParameter("compradorId", compradorId);
            return query.getResultList();
        }
    }
}
