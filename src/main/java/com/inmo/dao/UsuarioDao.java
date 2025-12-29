package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UsuarioDao {

    // ====== SELECTS ======

    public Optional<Usuario> findByEmail(String email) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            var q = s.createQuery(
                    "from Usuario u where lower(u.email) = :email", Usuario.class);
            q.setParameter("email", email == null ? "" : email.toLowerCase());
            return q.uniqueResultOptional();
        }
    }

    public List<Usuario> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Usuario u order by u.id", Usuario.class)
                    .getResultList();
        }
    }

    public Optional<Usuario> findById(Long id) {
        if (id == null) return Optional.empty();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(s.get(Usuario.class, id));
        }
    }

    // ====== INSERT / UPDATE ======

    /** Inserta si id==null, o actualiza si existe. Devuelve la entidad administrada. */
    public Usuario saveOrUpdate(Usuario u) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            if (u.getEmail() != null) u.setEmail(u.getEmail().trim().toLowerCase());

            Usuario managed = s.merge(u);
            tx.commit();
            return managed;
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    /** Envolturas para compatibilidad con el controller actual */
    public Usuario save(Usuario u)   { return saveOrUpdate(u); }
    public Usuario update(Usuario u) { return saveOrUpdate(u); }

    // ====== DELETE ======

    public void delete(Long id) {
        if (id == null) return;
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.createMutationQuery("delete from Usuario u where u.id = :id")
             .setParameter("id", id)
             .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    // ====== ESTADO (activar / inactivar) ======

    public void setActivo(Long id, boolean activo) {
        if (id == null) return;
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.createMutationQuery(
                "update Usuario u set u.activo = :activoChar, u.actualizadoEn = current_timestamp where u.id = :id")
             .setParameter("activoChar", activo ? 'S' : 'N')
             .setParameter("id", id)
             .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
