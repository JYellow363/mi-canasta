package com.micanasta.service.impl;

import com.micanasta.dto.CrearFamiliaDTO;
import com.micanasta.dto.FamiliaBusquedaMiembrosDto;
import com.micanasta.dto.HistorialDto;
import com.micanasta.dto.UsuarioPorFamiliaDto;
import com.micanasta.dto.converter.FamiliaDTOConverter;
import com.micanasta.dto.converter.UsuarioPorFamiliaDtoConverter;
import com.micanasta.exception.*;
import com.micanasta.model.*;
import com.micanasta.repository.*;
import com.micanasta.service.FamiliaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class FamiliaServiceImpl implements FamiliaService {

    @Autowired
    private FamiliaRepository familiaRepository;

    @Autowired
    private FamiliaDTOConverter familiaDTOConverter;

    @Autowired
    private UsuarioPorFamiliaRepository usuarioPorFamiliaRepository;

    @Autowired
    private UsuarioPorFamiliaDtoConverter usuarioPorFamiliaDtoConverter;

    @Autowired
    private RolPorUsuarioRepository rolPorUsuarioRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private HistorialRepository historialRepository;

    @Override
    @Transactional
    public Familia crearGrupoFamiliar(CrearFamiliaDTO familiaDTO) throws ExistingFamilyFoundException {
        if (familiaRepository.findByNombreUnico(familiaDTO.getNombreUnico()) != null) {
            throw new ExistingFamilyFoundException();
        } else {
            Familia familia = familiaDTOConverter.convertToEntity(familiaDTO);
            familia.setAceptacionSolicitudes(true);
            familia.setCantidad(1);
            familia = familiaRepository.save(familia);

            UsuarioPorFamilia usuarioPorFamilia = generarUsuarioPorFamilia(familiaDTO.getDni(), familia.getId());
            RolPorUsuario rolPorUsuario = asignarRolPorUsuario(familiaDTO.getDni(), (long) 1); // Asignación directa
            usuarioPorFamiliaRepository.save(usuarioPorFamilia);
            rolPorUsuarioRepository.save(rolPorUsuario);

            return familia;
        }
    }

    public Familia desactivarSolicitudes(String nombreFamilia, String dni) throws FamilyNotFoundException {
        Familia nombreFam;
        nombreFam = familiaRepository.findByNombreUnico(nombreFamilia);

        Optional<Solicitud> solicitud = solicitudRepository.findBySolicitudIdentityUsuarioDni(dni);

        if (nombreFam == null){
            throw new FamilyNotFoundException();
        } else {
            Familia familia = familiaRepository.findByNombreUnico(nombreFamilia);
            familia.setNombreUnico(nombreFamilia);
            familia.setAceptacionSolicitudes(false);
            familiaRepository.save(familia);

            solicitudRepository.findBySolicitudIdentityUsuarioDni(dni);
            if (solicitud.isPresent()){
                Solicitud solicitudes = solicitudRepository.findBySolicitudIdentityUsuarioDni(dni).get();
                solicitudRepository.delete(solicitudes);
            }
            return familia;
        }
    }

    @Override
    public List<FamiliaBusquedaMiembrosDto> buscarMiembrosGrupoFamiliarPorNombreFamilia(String nombreFamilia) {
        List<FamiliaBusquedaMiembrosDto> familiaBusquedaMiembrosDtos;

        Optional<List<UsuarioPorFamilia>> miembrosGrupoFamiliarPorFamilia = usuarioPorFamiliaRepository.findByUsuarioPorFamiliaIdentityFamiliaNombreUnico(nombreFamilia);

        if (miembrosGrupoFamiliarPorFamilia.isPresent() && miembrosGrupoFamiliarPorFamilia.get().size() > 0) {

            familiaBusquedaMiembrosDtos = miembrosGrupoFamiliarPorFamilia.get().stream()
                    .map((miembro) -> {
                        FamiliaBusquedaMiembrosDto familiaBusquedaMiembrosDto = new FamiliaBusquedaMiembrosDto();
                        familiaBusquedaMiembrosDto.setDni(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getDni());
                        familiaBusquedaMiembrosDto.setNombre(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getNombre());
                        familiaBusquedaMiembrosDto.setApellidoPaterno(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getApellidoPaterno());
                        familiaBusquedaMiembrosDto.setApellidoMaterno(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getApellidoMaterno());

                        return familiaBusquedaMiembrosDto;
                    })
                    .collect(Collectors.toList());
        } else {
            familiaBusquedaMiembrosDtos = null;
        }

        return familiaBusquedaMiembrosDtos;
    }

    private UsuarioPorFamilia generarUsuarioPorFamilia(String dni, Long id) {
        UsuarioPorFamilia usuarioPorFamilia = new UsuarioPorFamilia();
        UsuarioPorFamiliaIdentity usuarioPorFamiliaIdentity = new UsuarioPorFamiliaIdentity();
        Familia familia = new Familia();
        familia.setId(id);
        Usuario usuario = new Usuario();
        usuario.setDni(dni);
        usuarioPorFamiliaIdentity.setFamilia(familia);
        usuarioPorFamiliaIdentity.setUsuario(usuario);
        usuarioPorFamilia.setUsuarioPorFamiliaIdentity(usuarioPorFamiliaIdentity);

        return usuarioPorFamilia;
    }

    private RolPorUsuario asignarRolPorUsuario(String dni, Long id) {

        RolPerfil rolPerfil = new RolPerfil();
        rolPerfil.setId(id); // 1--> UsuarioPorFamilia, 2--> UsuarioPorTienda

        Usuario usuario = new Usuario();
        usuario.setDni(dni);

        RolPorUsuarioIdentity rolPorUsuarioIdentity = new RolPorUsuarioIdentity();
        rolPorUsuarioIdentity.setUsuario(usuario);
        rolPorUsuarioIdentity.setRolPerfil(rolPerfil);

        RolPorUsuario rolPorUsuario = new RolPorUsuario();
        rolPorUsuario.setRolPorUsuarioIdentity(rolPorUsuarioIdentity);

        return rolPorUsuario;

    }

    UsuarioPorFamilia asignarIdentitys(String userDni){
        UsuarioPorFamilia usuario = new UsuarioPorFamilia();
        Optional<UsuarioPorFamilia> usuarioPorFamilia =
                usuarioPorFamiliaRepository.findByUsuarioPorFamiliaIdentityUsuarioDni(userDni);

        UsuarioPorFamiliaIdentity usuarioPorFamiliaIdentity=new UsuarioPorFamiliaIdentity();

        usuarioPorFamiliaIdentity.setFamilia(usuarioPorFamilia.get().getUsuarioPorFamiliaIdentity().getFamilia());
        usuarioPorFamiliaIdentity.setUsuario(usuarioPorFamilia.get().getUsuarioPorFamiliaIdentity().getUsuario());
        usuario.setUsuarioPorFamiliaIdentity(usuarioPorFamiliaIdentity);
        return usuario;
    }

    @Transactional
    @Override
    public UsuarioPorFamiliaDto Remove(String adminDni, String userDni) throws UserNotAdminException, UserToDeleteIsAdminException {

        UsuarioPorFamiliaDto usuarioPorFamiliaDto=null;

        if(rolPorUsuarioRepository.findByRolPorUsuarioIdentityUsuarioDni(adminDni).getRolPorUsuarioIdentity().getRolPerfil().getId()!=1)
            throw new UserNotAdminException();
        else{
            if(rolPorUsuarioRepository.findByRolPorUsuarioIdentityUsuarioDni(userDni).getRolPorUsuarioIdentity().getRolPerfil().getId()==1)
                throw new UserToDeleteIsAdminException();
            else{

                UsuarioPorFamilia usuario = asignarIdentitys(userDni);

                usuarioPorFamiliaRepository.deleteByUsuarioPorFamiliaIdentityUsuarioDni(userDni);
                rolPorUsuarioRepository.deleteByRolPorUsuarioIdentityUsuarioDni(userDni);

                usuarioPorFamiliaDto = usuarioPorFamiliaDtoConverter.convertToDto(usuario);
                usuarioPorFamiliaDto.setDni(userDni);
            }
        }
        return usuarioPorFamiliaDto;
    }

    public boolean unicoAdmin(Familia familia) {
        int countAdmin=0;
        Optional<List<UsuarioPorFamilia>> miembrosGrupoFamiliarPorFamilia =
                usuarioPorFamiliaRepository.findByUsuarioPorFamiliaIdentityFamiliaNombreUnico(familia.getNombreUnico());

        List<FamiliaBusquedaMiembrosDto> familiaBusquedaMiembrosDtos = miembrosGrupoFamiliarPorFamilia.get().stream()
                .map((miembro) -> {
                    FamiliaBusquedaMiembrosDto familiaBusquedaMiembrosDto = new FamiliaBusquedaMiembrosDto();
                    familiaBusquedaMiembrosDto.setDni(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getDni());
                    familiaBusquedaMiembrosDto.setNombre(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getNombre());
                    familiaBusquedaMiembrosDto.setApellidoPaterno(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getApellidoPaterno());
                    familiaBusquedaMiembrosDto.setApellidoMaterno(miembro.getUsuarioPorFamiliaIdentity().getUsuario().getApellidoMaterno());

                    return familiaBusquedaMiembrosDto;
                })
                .collect(Collectors.toList());

        for (FamiliaBusquedaMiembrosDto miembro : familiaBusquedaMiembrosDtos) {
            if(rolPorUsuarioRepository.findByRolPorUsuarioIdentityUsuarioDni(miembro.getDni()).getRolPorUsuarioIdentity().getRolPerfil().getId()==1) {
                countAdmin++;
            }
        }
        if(countAdmin == 1) return true;
        else return false;
    }

// Cuando el usuario se intenta borrar a sí mismo
    @Transactional
    @Override
    public UsuarioPorFamiliaDto RemoveMyself(String nombreFamilia, String userDni) throws UserOnlyAdminException {

        UsuarioPorFamiliaDto usuarioPorFamiliaDto=null;
        UsuarioPorFamilia usuario = new UsuarioPorFamilia();

        if (usuarioPorFamiliaRepository.countByUsuarioPorFamiliaIdentityFamiliaNombreUnico(nombreFamilia)==1){

            usuario = asignarIdentitys(userDni);

            usuarioPorFamiliaRepository.deleteByUsuarioPorFamiliaIdentityUsuarioDni(userDni);
            rolPorUsuarioRepository.deleteByRolPorUsuarioIdentityUsuarioDni(userDni);
            familiaRepository.deleteByNombreUnico(nombreFamilia);

            usuarioPorFamiliaDto = usuarioPorFamiliaDtoConverter.convertToDto(usuario);
            usuarioPorFamiliaDto.setDni(userDni);
        }
        else{
            if(rolPorUsuarioRepository.findByRolPorUsuarioIdentityUsuarioDni(userDni).getRolPorUsuarioIdentity().getRolPerfil().getId()==1 && unicoAdmin(familiaRepository.findByNombreUnico(nombreFamilia))==true){
                throw new UserOnlyAdminException();
            }
            else{
                usuario = asignarIdentitys(userDni);

                usuarioPorFamiliaRepository.deleteByUsuarioPorFamiliaIdentityUsuarioDni(userDni);
                rolPorUsuarioRepository.deleteByRolPorUsuarioIdentityUsuarioDni(userDni);

                usuarioPorFamiliaDto = usuarioPorFamiliaDtoConverter.convertToDto(usuario);
                usuarioPorFamiliaDto.setDni(userDni);
            }
        }
        return usuarioPorFamiliaDto;
    }

    public List<Historial> filtrarPorFecha(String familiaNombre, Date fechaInicio, Date fechaFinal){
        List<Historial> historiales = historialRepository.getByHistorialIdentityFamiliaNombreUnico(familiaNombre);
        //List<Historial> historialesAux = historiales;
        //historialesAux.clear();

        /*historiales.stream().filter(x->x.getFechaCompra().after(fechaInicio)&&x.getFechaCompra().before(fechaFinal)
        ).forEach(x->historialesAux.add(x));
         */

        /*for (Historial historial : historiales.) {
            if(historial.getFechaCompra().after(fechaInicio) && historial.getFechaCompra().before(fechaFinal)){
                historialesAux.add(historial);
            }
        }*/
        return historiales;
    }

    @Override
    public List<HistorialDto> getHistorial(String familiaNombre, Date fechaInicio, Date fechaFinal){
        List<HistorialDto> historialesDto;
        //List<Historial> historiales=historialRepository.getByHistorialIdentityFamiliaNombreUnico(familiaNombre);

        List<Historial> historialesAux = historialRepository.getByHistorialIdentityFamiliaNombreUnico(familiaNombre);
        historialesAux.clear();


        for(Historial historial: historialRepository.getByHistorialIdentityFamiliaNombreUnico(familiaNombre)){
            if(historial.getFechaCompra().after(fechaInicio)&&historial.getFechaCompra().before(fechaFinal)){
                historialesAux.add(historial);
            }
        }

            historialesDto = historialesAux.stream().map(x -> {
                HistorialDto historialDto = new HistorialDto();
                historialDto.setDni(x.getDni());
                historialDto.setCantidad(x.getCantidad());
                historialDto.setFechaCompra(x.getFechaCompra());
                historialDto.setFamiliaId(x.getHistorialIdentity().getFamilia().getId());
                historialDto.setProductoId(x.getHistorialIdentity().getProducto().getId());
                historialDto.setTiendaId(x.getHistorialIdentity().getTienda().getId());
                return historialDto;
            }).collect(Collectors.toList());

        return historialesDto;
    }
}

