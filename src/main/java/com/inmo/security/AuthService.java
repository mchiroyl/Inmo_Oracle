package com.inmo.security;

import com.inmo.dao.UsuarioDao;
import com.inmo.domain.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UsuarioDao usuarioDao = new UsuarioDao();

    public Optional<Usuario> login(String email, String plainPassword) {
        if (email == null || plainPassword == null) return Optional.empty();

        var opt = usuarioDao.findByEmail(email);
        if (opt.isEmpty()) return Optional.empty();

        var u = opt.get();
        boolean activo = (u.getActivo() != null && u.getActivo() == 'S');
        if (!activo) return Optional.empty();

        boolean ok = BCrypt.checkpw(plainPassword, u.getHashPassword());
        return ok ? Optional.of(u) : Optional.empty();
    }
}
