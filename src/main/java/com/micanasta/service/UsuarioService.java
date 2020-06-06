package com.micanasta.service;

import com.micanasta.dto.*;
import com.micanasta.exception.*;

public interface UsuarioService {

    UsuarioDto save(UsuarioReniecDto usuario);

    UsuarioDto findByDni(String dni);

    UsuarioAccesoDto ValidateLogin(String dni, String contrasena)
            throws UserLoginIncorrectException, UserLoginNotFoundException;

    UsuarioDataDto GetDatosUsuario(String dni, String contrasena)throws UserLoginIncorrectException, UserLoginNotFoundException;

    UsuarioUpdateDto update(String dni, UsuarioUpdateDto usuarioUpdateDto)
            throws EmailWrongFormatException, NewPasswordNotMatchException, ActualPasswordNotMatchException;
    // Usuario validarDni(String dni);
    UsuarioFamiliaGetDto GetUsuarioFamilia(String dni) throws UserFamilyNotFoundException;
}
