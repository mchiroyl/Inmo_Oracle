package com.inmo.dao;

import com.inmo.config.HibernateUtil;
import com.inmo.domain.Inmueble;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class InmuebleDao {

    /** Lista todos los inmuebles (puedes ajustar el orden). */
    public List<Inmueble> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    select i
                    from Inmueble i
                    left join fetch i.vendedor v
                    order by i.id
                """, Inmueble.class).getResultList();
        }
    }

    /** Búsqueda general por término (dirección / tipo / email vendedor / nombre vendedor). */
    public List<Inmueble> search(String term) {
        String q = term == null ? "" : term.trim().toLowerCase();
        if (q.isBlank()) return findAll();

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    select i
                    from Inmueble i
                    left join fetch i.vendedor v
                    where lower(i.direccion) like :t
                       or lower(i.tipo)      like :t
                       or (v is not null and (
                               lower(v.email)  like :t
                            or lower(v.nombre) like :t
                       ))
                    order by i.id
                """, Inmueble.class)
                .setParameter("t", "%" + q + "%")
                .getResultList();
        }
    }

    /** Filtra por vendedor. */
    public List<Inmueble> findByVendedorId(Long vendedorId) {
        if (vendedorId == null) return List.of();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    select i
                    from Inmueble i
                    left join fetch i.vendedor v
                    where v.id = :vid
                    order by i.id
                """, Inmueble.class)
                .setParameter("vid", vendedorId)
                .getResultList();
        }
    }

    /** Inserta/actualiza. */
    public void save(Inmueble i) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.merge(i);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    /** Elimina por ID. */
    public void deleteById(Long id) {
        if (id == null) return;
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Inmueble managed = s.find(Inmueble.class, id);
            if (managed != null) s.remove(managed);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    /** (Opcional) Elimina recibiendo la entidad. */
    public void delete(Inmueble i) {
        if (i == null || i.getId() == null) return;
        deleteById(i.getId());
    }

    /** Obtiene por id. */
    public Inmueble findById(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.find(Inmueble.class, id);
        }
    }

    /** Búsqueda avanzada por múltiples criterios. */
    public List<Inmueble> buscarPorCriterios(String tipo, BigDecimal precioMin, BigDecimal precioMax,
                                             BigDecimal metrajeMin, String condicion, String estado) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("SELECT i FROM Inmueble i WHERE 1=1");

            if (tipo != null && !tipo.trim().isEmpty()) {
                hql.append(" AND i.tipo = :tipo");
            }
            if (precioMin != null) {
                hql.append(" AND i.precio >= :precioMin");
            }
            if (precioMax != null) {
                hql.append(" AND i.precio <= :precioMax");
            }
            if (metrajeMin != null) {
                hql.append(" AND i.metraje >= :metrajeMin");
            }
            if (condicion != null && !condicion.trim().isEmpty()) {
                hql.append(" AND i.condicion = :condicion");
            }
            if (estado != null && !estado.trim().isEmpty()) {
                hql.append(" AND i.estado = :estado");
            }

            hql.append(" ORDER BY i.precio ASC");

            TypedQuery<Inmueble> query = s.createQuery(hql.toString(), Inmueble.class);

            if (tipo != null && !tipo.trim().isEmpty()) {
                query.setParameter("tipo", tipo.toUpperCase());
            }
            if (precioMin != null) {
                query.setParameter("precioMin", precioMin);
            }
            if (precioMax != null) {
                query.setParameter("precioMax", precioMax);
            }
            if (metrajeMin != null) {
                query.setParameter("metrajeMin", metrajeMin);
            }
            if (condicion != null && !condicion.trim().isEmpty()) {
                query.setParameter("condicion", condicion.toUpperCase());
            }
            if (estado != null && !estado.trim().isEmpty()) {
                query.setParameter("estado", estado.toUpperCase());
            }

            return query.getResultList();
        }
    }

    /** Lista inmuebles disponibles. */
    public List<Inmueble> findDisponibles() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                    SELECT i FROM Inmueble i
                    WHERE i.estado = 'DISPONIBLE'
                    ORDER BY i.creadoEn DESC
                """, Inmueble.class).getResultList();
        }
    }
}
