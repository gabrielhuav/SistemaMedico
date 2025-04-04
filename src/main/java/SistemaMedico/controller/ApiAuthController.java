package SistemaMedico.controller;

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

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Rol;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.UsuarioRepository;
import SistemaMedico.service.CustomUserDetailsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
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

    @Autowired
    private CitaRepository citaRepository;
    

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
            return ResponseEntity.status(HttpStatus.OK).body("Usuario eliminado exitosamente");
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
   @PostMapping("/citas")
   public ResponseEntity<String> crearCita(@RequestBody Cita cita) {
       try {
           // Validar que el paciente y el doctor existan
           if (cita.getIdPaciente() == null || cita.getIdDoctor() == null) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID del paciente y del doctor son obligatorios");
           }
   
           Optional<Usuario> paciente = userRepository.findById(cita.getIdPaciente());
           Optional<Usuario> doctor = userRepository.findById(cita.getIdDoctor());
   
           if (paciente.isEmpty() || doctor.isEmpty()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Paciente o doctor no encontrado");
           }
   
           // Validar el formato de fecha y hora
           if (cita.getFechaHora() == null) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fecha y hora son obligatorias");
           }
   
           // Guardar la cita
           cita.setEstado("pendiente");
           citaRepository.save(cita);
   
           return ResponseEntity.status(HttpStatus.CREATED).body("Cita creada exitosamente");
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la cita");
       }
   }
    
}
