package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Agente;
import com.inmo.domain.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AgenteDao {

    public List<Agente> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Agente a order by a.nombre", Agente.class).getResultList();
        }
    }

    public List<Agente> findAllActivos() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                from Agente a
                where a.activo = 'S'
                order by a.nombre
            """, Agente.class).getResultList();
        }
    }

    public List<Agente> search(String term) {
        String q = (term == null) ? "" : term.trim().toLowerCase();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                        from Agente a
                        where lower(a.nombre) like :t
                           or lower(a.email)  like :t
                           or lower(a.telefono) like :t
                        order by a.nombre
                    """, Agente.class)
                    .setParameter("t", "%" + q + "%")
                    .getResultList();
        }
    }

    public void save(Agente a) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            if (a.getEmail() != null) a.setEmail(a.getEmail().trim().toLowerCase());
            if (a.getNombre()!= null) a.setNombre(a.getNombre().trim());
            s.merge(a);
            // (vinculación con USUARIO rol AGENTE la puedes mantener como ya la tenías)
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    public void delete(Agente a) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Agente m = s.find(Agente.class, a.getId());
            if (m != null) s.remove(m);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    public Agente findById(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.find(Agente.class, id);
        }
    }

    public Agente findByEmail(String email) {
        if (email == null) return null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    from Agente a where lower(a.email) = :e
                """, Agente.class)
                .setParameter("e", email.trim().toLowerCase())
                .setMaxResults(1)
                .uniqueResult();
        }
    }

    public Agente findByUsuarioId(Long usuarioId) {
        if (usuarioId == null) return null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    from Agente a
                    where a.usuario.id = :uid
                """, Agente.class)
                .setParameter("uid", usuarioId)
                .setMaxResults(1)
                .uniqueResult();
        }
    }
}
