package com.project.minhasfinancias.service;

import com.project.minhasfinancias.model.entity.Usuario;

public interface UsuarioService {
    Usuario autenticar(String email, String senha);

    Usuario SalvaUsuario(Usuario usuario);

    void validarEmail(String email);
}
