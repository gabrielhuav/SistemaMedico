package SistemaMedico;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Rol;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.RolRepository;
import SistemaMedico.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para ApiAuthController.
 * Requiere una base de datos de prueba (H2 o similar).
 */
@SpringBootTest(classes = SistemaMedicoApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiAuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario testUser;
    private Usuario testDoctor;
    private Rol userRole;
    private Rol doctorRole;

    @BeforeAll
    void setupRoles() {
        usuarioRepository.deleteAll();
        citaRepository.deleteAll();
        rolRepository.deleteAll();

        userRole = new Rol();
        userRole.setNombre("ROLE_USER");
        userRole = rolRepository.save(userRole);

        doctorRole = new Rol();
        doctorRole.setNombre("ROLE_DOCTOR");
        doctorRole = rolRepository.save(doctorRole);
    }

    @BeforeEach
    void setupUser() {
        usuarioRepository.deleteAll();
        citaRepository.deleteAll();

        testUser = new Usuario();
        testUser.setNombre("paciente");
        testUser.setEmail("paciente@example.com");
        testUser.setPassword(passwordEncoder.encode("testpass"));
        testUser.setRoles(Set.of(userRole));
        testUser = usuarioRepository.save(testUser);

        testDoctor = new Usuario();
        testDoctor.setNombre("doctor");
        testDoctor.setEmail("doctor@example.com");
        testDoctor.setPassword(passwordEncoder.encode("testpass"));
        testDoctor.setRoles(Set.of(doctorRole));
        testDoctor = usuarioRepository.save(testDoctor);
    }

    @Test
    void welcome_debeResponderApiRunning() throws Exception {
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(content().string("API is running!"));
    }

    @Test
    void register_y_login_usuario() throws Exception {
        Usuario nuevo = new Usuario();
        nuevo.setNombre("Nuevo");
        nuevo.setEmail("nuevo@example.com");
        nuevo.setPassword("abc123");

        // Registro
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Usuario registrado")));

        // Login exitoso
        LoginCredentials cred = new LoginCredentials();
        cred.setCorreo("nuevo@example.com");
        cred.setPassword("abc123");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cred)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login successful")));

        // Login fallido
        cred.setPassword("wrongpass");
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cred)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid login credentials")));
    }

    @Test
    void register_usuario_existente() throws Exception {
        Usuario repetido = new Usuario();
        repetido.setNombre("Repetido");
        repetido.setEmail(testUser.getEmail());
        repetido.setPassword("algo");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(repetido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("correo ya está en uso")));
    }

    @Test
    void crud_usuario() throws Exception {
        // Listar usuarios
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Eliminar usuario existente
        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("eliminado exitosamente")));

        // Eliminar usuario inexistente
        mockMvc.perform(delete("/api/users/999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("no encontrado")));
    }

    @Test
    void crearCita_y_validaciones() throws Exception {
        // Cita válida
        Cita cita = new Cita();
        cita.setIdPaciente(testUser.getId());
        cita.setIdDoctor(testDoctor.getId());
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setMotivo("Control");

        mockMvc.perform(post("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cita)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Cita creada exitosamente")));

        // Sin paciente o doctor
        cita.setIdPaciente(null);
        mockMvc.perform(post("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cita)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("ID del paciente y del doctor")));

        // Paciente o doctor no existen
        cita.setIdPaciente(999L);
        cita.setIdDoctor(999L);
        mockMvc.perform(post("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cita)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Paciente o doctor no encontrado")));

        // Sin fecha
        cita.setIdPaciente(testUser.getId());
        cita.setIdDoctor(testDoctor.getId());
        cita.setFechaHora(null);
        mockMvc.perform(post("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cita)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("fecha y hora son obligatorias")));
    }

    @Test
    void toggleAdmin_doctorRole_usuario() throws Exception {
        // Toggle admin
        mockMvc.perform(post("/api/users/" + testUser.getId() + "/toggle-admin"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("admin actualizado")));

        Usuario actualizado = usuarioRepository.findById(testUser.getId()).get();
        assertTrue(actualizado.getRoles().stream().anyMatch(r -> r.getNombre().equals("ROLE_ADMIN")));

        // Toggle admin otra vez (remueve)
        mockMvc.perform(post("/api/users/" + testUser.getId() + "/toggle-admin"))
                .andExpect(status().isOk());

        actualizado = usuarioRepository.findById(testUser.getId()).get();
        assertFalse(actualizado.getRoles().stream().anyMatch(r -> r.getNombre().equals("ROLE_ADMIN")));
    }

    @Test
    void toggleDoctor_doctorRole_usuario() throws Exception {
        // Toggle doctor
        mockMvc.perform(post("/api/users/" + testUser.getId() + "/toggle-doctor"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("doctor actualizado")));

        Usuario actualizado = usuarioRepository.findById(testUser.getId()).get();
        assertTrue(actualizado.getRoles().stream().anyMatch(r -> r.getNombre().equals("ROLE_DOCTOR")));

        // Toggle doctor otra vez (remueve)
        mockMvc.perform(post("/api/users/" + testUser.getId() + "/toggle-doctor"))
                .andExpect(status().isOk());

        actualizado = usuarioRepository.findById(testUser.getId()).get();
        assertFalse(actualizado.getRoles().stream().anyMatch(r -> r.getNombre().equals("ROLE_DOCTOR")));
    }

    // Clase interna para credenciales de login
    static class LoginCredentials {
        private String correo;
        private String password;

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}