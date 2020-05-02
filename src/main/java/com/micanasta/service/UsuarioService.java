package com.micanasta.service;

import com.micanasta.dto.UsuarioDto;
import com.micanasta.dto.UsuarioReniecDto;
import com.micanasta.model.Usuario;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UsuarioService {

    UsuarioDto save(UsuarioReniecDto usuario);
    UsuarioDto findByDni (String dni);

    //Usuario validarDni(String dni);

}
