package HolaSpring6CV3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import HolaSpring6CV3.entity.Rol;
import HolaSpring6CV3.entity.Usuario;
import HolaSpring6CV3.repository.UsuarioRepository;
import HolaSpring6CV3.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("API is running!");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginCredentials credentials) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getCorreo(), credentials.getPassword())
            );

            // Retornar un mensaje o un token según sea necesario
            return ResponseEntity.ok("Login successful for API");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Usuario user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo ya está en uso");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<Iterable<Usuario>> getAllUsers() {
        Iterable<Usuario> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    @PutMapping("/users/{id}/toggle-admin")
public ResponseEntity<String> toggleAdminRole(@PathVariable Long id) {
    return userRepository.findById(id).map(user -> {
        boolean isAdmin = user.getRoles().stream().anyMatch(rol -> rol.getNombre().equals("ROLE_ADMIN"));
        Rol adminRole = new Rol();
        adminRole.setNombre("ROLE_ADMIN");

        if (isAdmin) {
            user.getRoles().removeIf(rol -> rol.getNombre().equals("ROLE_ADMIN"));
        } else {
            user.getRoles().add(adminRole);
        }

        userRepository.save(user);
        return ResponseEntity.ok("Rol de admin actualizado");
    }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
}

    
}
