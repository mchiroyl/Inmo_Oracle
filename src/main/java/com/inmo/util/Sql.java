package com.inmo.util;

import com.inmo.config.HibernateUtil;
import com.inmo.dto.VendedorItem;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Consultas SQL nativas puntuales que no ameritan un DAO dedicado.
 * Mantén este utilitario ENFOCADO en lecturas simples.
 */
public class Sql {

    /**
     * Lista de vendedores según modo:
     *   "ALL"              -> todos
     *   "AGENTE_USUARIO"   -> vendedores de la cartera del agente (si aplica en tu modelo)
     *   "VENDEDOR_USUARIO" -> solo el vendedor asociado al usuario logueado
     *
     * @param mode   modo de filtrado
     * @param userId id del USUARIO en sesión
     * @return lista de VendedorItem {id, nombre/código}
     */
    @SuppressWarnings("unchecked")
    public static List<VendedorItem> readVendedores(String mode, Long userId) {
        List<Object[]> rows = new ArrayList<>();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {

            if ("AGENTE_USUARIO".equals(mode)) {
                // Ajusta a tu esquema real si tienes relación AGENTE->VENDEDOR
                rows = s.createNativeQuery("""
                        SELECT v.id, NVL(v.nombre, v.codigo) AS nombre
                        FROM   VENDEDOR v
                        WHERE  v.AGENTE_ID IN (SELECT a.id FROM AGENTE a WHERE a.USUARIO_ID = :uid)
                        ORDER  BY nombre
                        """)
                        .setParameter("uid", userId)
                        .getResultList();

            } else if ("VENDEDOR_USUARIO".equals(mode)) {
                rows = s.createNativeQuery("""
                        SELECT v.id, NVL(v.nombre, v.codigo) AS nombre
                        FROM   VENDEDOR v
                        WHERE  v.USUARIO_ID = :uid
                        ORDER  BY nombre
                        """)
                        .setParameter("uid", userId)
                        .getResultList();

            } else { // ALL
                rows = s.createNativeQuery("""
                        SELECT v.id, NVL(v.nombre, v.codigo) AS nombre
                        FROM   VENDEDOR v
                        ORDER  BY nombre
                        """)
                        .getResultList();
            }
        }

        List<VendedorItem> out = new ArrayList<>();
        for (Object[] r : rows) {
            Long id = ((Number) r[0]).longValue();
            String nombre = (String) r[1];
            out.add(new VendedorItem(id, nombre));
        }
        return out;
    }

    /** Devuelve el nombre del vendedor (o null). */
    public static String readVendedorNombre(Long vendedorId) {
        if (vendedorId == null) return null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Object r = s.createNativeQuery("""
                    SELECT NVL(nombre, codigo) FROM VENDEDOR WHERE id = :id
                    """)
                    .setParameter("id", vendedorId)
                    .getSingleResult();
            return r == null ? null : r.toString();
        }
    }

    /** Obtiene el ID de VENDEDOR por ID de USUARIO (para rol VENDEDOR). */
    public static Long readVendedorIdByUsuario(Long usuarioId) {
        if (usuarioId == null) return null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Object r = s.createNativeQuery("""
                    SELECT id FROM VENDEDOR WHERE USUARIO_ID = :uid
                    """)
                    .setParameter("uid", usuarioId)
                    .getSingleResult();
            return r == null ? null : ((Number) r).longValue();
        } catch (Exception ex) {
            return null; // si no hay fila
        }
    }
}
