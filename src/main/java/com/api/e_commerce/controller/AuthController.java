package com.api.e_commerce.controller;

import com.api.e_commerce.model.Usuario;
import com.api.e_commerce.repository.UsuarioRepository;
import com.api.e_commerce.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // permitir peticiones del frontend
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil,
            UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Registro ---
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Usuario nuevoUsuario) {
        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            return Map.of("error", "El email ya está registrado");
        }
        nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
        usuarioRepository.save(nuevoUsuario);
        return Map.of("mensaje", "Usuario registrado con éxito");
    }

    // --- Login ---
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("nombreUsuario");
            String password = datos.get("contrasena");

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // Obtener los roles del usuario autenticado
            var roles = auth.getAuthorities().stream()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .collect(java.util.stream.Collectors.toSet());

            String token = jwtUtil.generateToken(username, roles);
            return Map.of("token", token);

        } catch (AuthenticationException e) {
            return Map.of("error", "Credenciales inválidas");
        }
    }
}
