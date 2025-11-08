package com.api.e_commerce.security;

import com.api.e_commerce.model.Usuario;
import com.api.e_commerce.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga los detalles del usuario para Spring Security.
 * Se usa durante el proceso de autenticación para obtener el usuario desde la
 * base de datos.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su email (username).
     * Spring Security llama a este método durante el proceso de autenticación.
     * 
     * @param username El email del usuario (usado como username)
     * @return UserDetails con la información del usuario (nuestro Usuario ya
     *         implementa UserDetails)
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar el usuario por email (que usamos como username)
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Como Usuario implementa UserDetails, podemos devolverlo directamente
        // Esto incluye automáticamente el rol del usuario a través del método
        // getAuthorities()
        return usuario;
    }
}
