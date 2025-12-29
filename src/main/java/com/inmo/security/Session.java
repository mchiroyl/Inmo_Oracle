package com.inmo.security;

import com.inmo.domain.Usuario;

import java.util.Optional;

public final class Session {
    private static Usuario current;

    private Session(){}

    public static void setCurrent(Usuario u) { current = u; }
    public static Optional<Usuario> getCurrent() { return Optional.ofNullable(current); }
    public static void clear() { current = null; }
}
