package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Vendedor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VendedorDao {

    /** Lista todos los vendedores (sin filtrar), ordenados por nombre. */
    public List<Vendedor> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    from Vendedor v
                    order by v.nombre
                """, Vendedor.class).getResultList();
        }
    }

    /** Lista activos. */
    public List<Vendedor> findAllActivos() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    from Vendedor v
                    where v.activo = 'S'
                    order by v.nombre
                """, Vendedor.class).getResultList();
        }
    }

    /** Por Agente. */
    public List<Vendedor> findByAgenteId(Long agenteId) {
        if (agenteId == null) return List.of();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    from Vendedor v
                    where v.agente.id = :aid
                    order by v.nombre
                """, Vendedor.class)
                .setParameter("aid", agenteId)
                .getResultList();
        }
    }

    /** Por Usuario. */
    public Optional<Vendedor> findByUsuarioId(Long usuarioId) {
        if (usuarioId == null) return Optional.empty();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Vendedor vendedor = s.createQuery("""
                    from Vendedor v
                    where v.usuario.id = :uid
                """, Vendedor.class)
                .setParameter("uid", usuarioId)
                .setMaxResults(1)
                .uniqueResult();
            return Optional.ofNullable(vendedor);
        }
    }

    /** Búsqueda general por nombre/email/teléfono. */
    public List<Vendedor> search(String term) {
        String q = (term == null) ? "" : term.trim().toLowerCase();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    from Vendedor v
                    where lower(v.nombre) like :t
                       or lower(v.email) like :t
                       or lower(v.telefono) like :t
                    order by v.nombre
                """, Vendedor.class)
                .setParameter("t", "%" + q + "%")
                .getResultList();
        }
    }

    /** Guardar (insert/update). */
    public void save(Vendedor v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            if (v.getEmail() != null) v.setEmail(v.getEmail().trim().toLowerCase());
            if (v.getNombre() != null) v.setNombre(v.getNombre().trim());
            if (v.getTelefono() != null) v.setTelefono(v.getTelefono().trim());

            s.merge(v);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    /** Eliminar. */
    public void delete(Vendedor v) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Vendedor managed = s.find(Vendedor.class, v.getId());
            if (managed != null) {
                s.remove(managed);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
