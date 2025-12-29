package com.inmo.security;

import com.inmo.domain.Usuario;

/**
 * Utilidades de autorización / permisos.
 * - Mantiene compatibilidad hacia atrás con los métodos existentes.
 * - Agrega helpers por rol y permisos para Inmuebles.
 */
public final class Access {

    private Access() {}

    // ------------------------
    // Helpers de sesión actual
    // ------------------------

    /** Devuelve true si hay usuario logueado en Session */
    public static boolean isLogged() {
        return Session.getCurrent().isPresent();
    }

    /** True si el usuario actual es ADMIN (compatibilidad existente) */
    public static boolean isAdmin() {
        return Session.getCurrent()
                .map(u -> Usuario.ROL_ADMIN.equalsIgnoreCase(u.getRol()))
                .orElse(false);
    }

    /** Solo ADMIN puede gestionar usuarios (compatibilidad existente) */
    public static boolean canManageUsers() {
        return isAdmin();
    }

    // ------------------------
    // Helpers por rol (con y sin parámetro)
    // ------------------------

    public static boolean isAdmin(Usuario u) {
        return u != null && Usuario.ROL_ADMIN.equalsIgnoreCase(u.getRol());
    }

    public static boolean isAgente(Usuario u) {
        return u != null && Usuario.ROL_AGENTE.equalsIgnoreCase(u.getRol());
    }

    public static boolean isVendedor(Usuario u) {
        return u != null && Usuario.ROL_VENDEDOR.equalsIgnoreCase(u.getRol());
    }

    public static boolean isComprador(Usuario u) {
        return u != null && Usuario.ROL_COMPRADOR.equalsIgnoreCase(u.getRol());
    }

    // Versiones sin parámetro (usan la sesión actual)
    public static boolean isAgente()   { return Session.getCurrent().map(Access::isAgente).orElse(false); }
    public static boolean isVendedor() { return Session.getCurrent().map(Access::isVendedor).orElse(false); }
    public static boolean isComprador(){ return Session.getCurrent().map(Access::isComprador).orElse(false); }

    // ---------------------------------
    // Permisos específicos de Inmuebles
    // ---------------------------------

    /** Admin y Agente pueden gestionar todos los inmuebles */
    public static boolean canManageAllInmuebles(Usuario u) {
        return isAdmin(u) || isAgente(u);
    }

    /** Variante sin parámetro: usa sesión actual */
    public static boolean canManageAllInmuebles() {
        return Session.getCurrent().map(Access::canManageAllInmuebles).orElse(false);
    }

    /** Crear inmueble: Admin/Agente/Vendedor */
    public static boolean canCreateInmueble(Usuario u) {
        return isAdmin(u) || isAgente(u) || isVendedor(u);
    }

    public static boolean canCreateInmueble() {
        return Session.getCurrent().map(Access::canCreateInmueble).orElse(false);
    }

    /**
     * Editar inmueble:
     *  - Admin/Agente: siempre
     *  - Vendedor: solo si el inmueble pertenece a su VENDEDOR_ID
     */
    public static boolean canEditInmueble(Usuario u, Long vendedorIdDelInmueble, Long vendedorIdDelUsuario) {
        if (canManageAllInmuebles(u)) return true;
        return isVendedor(u)
                && vendedorIdDelInmueble != null
                && vendedorIdDelUsuario != null
                && vendedorIdDelInmueble.longValue() == vendedorIdDelUsuario.longValue();
    }

    /** Eliminar inmueble: mismas reglas que editar */
    public static boolean canDeleteInmueble(Usuario u, Long vendedorIdDelInmueble, Long vendedorIdDelUsuario) {
        return canEditInmueble(u, vendedorIdDelInmueble, vendedorIdDelUsuario);
    }

    /**
     * Sistema simple de permisos por recurso y acción.
     * Siempre devuelve true para ADMIN.
     * Para otros roles, evalúa según la lógica de negocio.
     */
    public static boolean can(Usuario u, String recurso, String accion) {
        if (u == null) return false;
        if (isAdmin(u)) return true;

        // Definir permisos según recurso y rol
        switch (recurso.toUpperCase()) {
            case "USUARIO":
                return isAdmin(u);

            case "AGENTE":
                return isAdmin(u) || isAgente(u);

            case "VENDEDOR":
                return isAdmin(u) || isAgente(u);

            case "COMPRADOR":
                switch (accion.toUpperCase()) {
                    case "READ":
                        return true; // Todos pueden leer
                    case "CREATE":
                    case "UPDATE":
                    case "DELETE":
                        return isAdmin(u) || isAgente(u);
                    default:
                        return false;
                }

            case "INMUEBLE":
                switch (accion.toUpperCase()) {
                    case "READ":
                        return true; // Todos pueden leer
                    case "CREATE":
                        return isAdmin(u) || isAgente(u) || isVendedor(u);
                    case "UPDATE":
                    case "DELETE":
                        return isAdmin(u) || isAgente(u) || isVendedor(u);
                    default:
                        return false;
                }

            case "OFERTA":
                switch (accion.toUpperCase()) {
                    case "READ":
                        return true; // Todos pueden leer sus propias ofertas
                    case "CREATE":
                        return isAdmin(u) || isComprador(u) || isAgente(u);
                    case "UPDATE":
                    case "DELETE":
                        return isAdmin(u) || isComprador(u) || isAgente(u);
                    default:
                        return false;
                }

            default:
                return false;
        }
    }
}
