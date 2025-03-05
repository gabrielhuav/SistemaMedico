package HolaSpring6CV3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration

public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        
            .csrf().disable()  // Desactiva CSRF para facilitar el acceso desde la aplicación móvil
            .authorizeHttpRequests((requests) -> requests
            .requestMatchers("/api/auth/**").permitAll()  // Permitir acceso sin autenticación a la app móvil
            .requestMatchers("/admin").hasRole("ADMIN")  // Acceso solo para usuarios con rol ADMIN a la ruta /admin
            .requestMatchers("/perfil").authenticated()
           // .requestMatchers("/admin/eliminar/**").hasRole("ADMIN")  // Permite que los usuarios autenticados puedan eliminar registros
            .requestMatchers("/home").authenticated()  // Requiere autenticación para acceder a /home
                .anyRequest().permitAll()
            )
            .formLogin((form) -> form
                .loginPage("/login")  // Mantiene el form login para el navegador
                .defaultSuccessUrl("/home", true)  // Redirige a /home tras login exitoso
                .permitAll()
            )
            // .formLogin(login -> login
            // .loginPage("/login/admin")
            // .defaultSuccessUrl("/admin/usuarios", true)
            // .permitAll()
            // )
            .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
