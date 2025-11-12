package com.api.e_commerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad con reglas de autorización
     * basadas en roles.
     * Define qué endpoints son públicos, cuáles requieren autenticación y cuáles
     * son solo para admins.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF (necesario para APIs REST)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita CORS
                .authorizeHttpRequests(auth -> auth
                        // ========== ENDPOINTS PÚBLICOS (sin autenticación) ==========
                        // Autenticación: Registro y Login
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()

                        // Productos: Solo lectura para todos (GET)
                        .requestMatchers(HttpMethod.GET, "/api/productos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/categoria/{categoryId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/buscar").permitAll()

                        // Categorías: Solo lectura para todos (GET)
                        .requestMatchers(HttpMethod.GET, "/api/categorias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/{id}").permitAll()

                        // ========== ENDPOINTS SOLO PARA ADMINISTRADORES ==========
                        // Productos: Crear, actualizar y eliminar (solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/{id}").hasRole("ADMIN")

                        // Categorías: Crear, actualizar y eliminar (solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/categorias").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/{id}").hasRole("ADMIN")

                        // ========== ENDPOINTS PARA USUARIOS AUTENTICADOS ==========
                        // Pedidos: Crear pedido (checkout) y consultar pedidos
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/checkout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/{id}").authenticated()

                        // ========== CUALQUIER OTRA RUTA ==========
                        // Por defecto, cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin sesiones
                                                                                                        // (JWT)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Añade el filtro JWT

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // encripta contraseñas
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
