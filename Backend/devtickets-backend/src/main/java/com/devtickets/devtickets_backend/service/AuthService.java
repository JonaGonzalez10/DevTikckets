package com.devtickets.devtickets_backend.service;

import com.devtickets.devtickets_backend.dto.auth.LoginRequest;
import com.devtickets.devtickets_backend.dto.auth.LoginResponse;
import com.devtickets.devtickets_backend.dto.auth.RegistroRequest;
import com.devtickets.devtickets_backend.exception.ApiException;
import com.devtickets.devtickets_backend.model.Rol;
import com.devtickets.devtickets_backend.model.Usuario;
import com.devtickets.devtickets_backend.repository.RolRepository;
import com.devtickets.devtickets_backend.repository.UsuarioRepository;
import com.devtickets.devtickets_backend.security.JwtService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String ROL_CLIENTE = "CLIENTE";

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Lanza AuthenticationException si las credenciales son invalidas o el usuario esta inactivo
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.correoElectronico(), request.contrasena()));

        Usuario usuario = usuarioRepository.findByCorreoElectronico(request.correoElectronico())
                .orElseThrow();
        return construirRespuesta(usuario);
    }

    @Transactional
    public LoginResponse registrar(RegistroRequest request) {
        String correo = request.correoElectronico().trim();
        if (usuarioRepository.findByCorreoElectronico(correo).isPresent()) {
            throw ApiException.conflicto("El correo ya esta registrado");
        }

        Rol rolCliente = rolRepository.findByNombre(ROL_CLIENTE).orElseGet(() -> {
            Rol nuevo = new Rol();
            nuevo.setNombre(ROL_CLIENTE);
            return rolRepository.save(nuevo);
        });

        Usuario usuario = new Usuario();
        usuario.setCorreoElectronico(correo);
        usuario.setHashContrasena(passwordEncoder.encode(request.contrasena()));
        usuario.setNombre(request.nombre().trim());
        usuario.setApellido(request.apellido().trim());
        usuario.setTelefono(request.telefono());
        usuario.setEstado(Usuario.ESTADO_ACTIVO);
        usuario.getRoles().add(rolCliente);
        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            throw ApiException.conflicto("El correo ya esta registrado");
        }
        return construirRespuesta(usuario);
    }

    private LoginResponse construirRespuesta(Usuario usuario) {
        List<String> roles = usuario.getRoles().stream().map(Rol::getNombre).sorted().toList();
        return new LoginResponse(
                jwtService.generar(usuario.getCorreoElectronico(), roles),
                jwtService.getExpiracion().toSeconds(),
                usuario.getId(),
                usuario.getCorreoElectronico(),
                usuario.getNombre(),
                usuario.getApellido(),
                roles);
    }
}
