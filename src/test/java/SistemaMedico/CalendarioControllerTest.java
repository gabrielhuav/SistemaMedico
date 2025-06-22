package SistemaMedico;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.service.UsuarioService;
import SistemaMedico.controller.CalendarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalendarioControllerTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private CalendarioController calendarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mostrarCalendario_devuelveNombreVista() {
        String vista = calendarioController.mostrarCalendario();
        assertEquals("calendario", vista);
    }

    @Test
    void obtenerEventos_usuarioLogeado_conCitas() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Paciente");

        // Citas como paciente
        Cita citaPaciente = new Cita();
        citaPaciente.setId(10L);
        citaPaciente.setIdDoctor(2L);
        citaPaciente.setFechaHora(LocalDateTime.of(2025, 7, 1, 10, 0));
        citaPaciente.setEstado("pendiente");

        // Citas como doctor
        Cita citaDoctor = new Cita();
        citaDoctor.setId(20L);
        citaDoctor.setIdPaciente(3L);
        citaDoctor.setFechaHora(LocalDateTime.of(2025, 7, 2, 14, 0));
        citaDoctor.setEstado("confirmada");

        // Mock usuario actual y citas
        when(usuarioService.obtenerUsuarioActual()).thenReturn(usuario);
        when(citaRepository.findByIdPaciente(1L)).thenReturn(List.of(citaPaciente));
        when(citaRepository.findByIdDoctor(1L)).thenReturn(List.of(citaDoctor));

        // Mock nombres
        Usuario doctor = new Usuario();
        doctor.setId(2L);
        doctor.setNombre("Dr. Lopez");
        Usuario paciente = new Usuario();
        paciente.setId(3L);
        paciente.setNombre("Juan Pérez");

        when(usuarioService.encontrarPorId(2L)).thenReturn(Optional.of(doctor));
        when(usuarioService.encontrarPorId(3L)).thenReturn(Optional.of(paciente));

        List<Map<String, Object>> eventos = calendarioController.obtenerEventos();

        assertEquals(2, eventos.size());

        Map<String, Object> eventoPaciente = eventos.get(0);
        assertEquals("Cita con el Dr./Dra. Dr. Lopez", eventoPaciente.get("title"));
        assertEquals("pendiente", eventoPaciente.get("estado"));
        assertTrue(eventoPaciente.get("start").toString().contains("2025-07-01T10:00"));

        Map<String, Object> eventoDoctor = eventos.get(1);
        assertEquals("Cita con el Paciente Juan Pérez", eventoDoctor.get("title"));
        assertEquals("confirmada", eventoDoctor.get("estado"));
        assertTrue(eventoDoctor.get("start").toString().contains("2025-07-02T14:00"));
    }

    @Test
    void obtenerEventos_usuarioNoLogeado() {
        when(usuarioService.obtenerUsuarioActual()).thenReturn(null);

        List<Map<String, Object>> eventos = calendarioController.obtenerEventos();

        assertNotNull(eventos);
        assertTrue(eventos.isEmpty());
    }

    @Test
    void obtenerEventos_doctorOPacienteNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        // Cita como paciente y doctor
        Cita citaPaciente = new Cita();
        citaPaciente.setIdDoctor(2L);
        citaPaciente.setFechaHora(LocalDateTime.now());
        citaPaciente.setEstado("pendiente");

        Cita citaDoctor = new Cita();
        citaDoctor.setIdPaciente(3L);
        citaDoctor.setFechaHora(LocalDateTime.now());
        citaDoctor.setEstado("confirmada");

        when(usuarioService.obtenerUsuarioActual()).thenReturn(usuario);
        when(citaRepository.findByIdPaciente(1L)).thenReturn(List.of(citaPaciente));
        when(citaRepository.findByIdDoctor(1L)).thenReturn(List.of(citaDoctor));
        when(usuarioService.encontrarPorId(2L)).thenReturn(Optional.empty());
        when(usuarioService.encontrarPorId(3L)).thenReturn(Optional.empty());

        List<Map<String, Object>> eventos = calendarioController.obtenerEventos();

        assertEquals(2, eventos.size());
        // Si no se encuentra el doctor
        assertTrue(eventos.get(0).get("title").toString().contains("Doctor #2"));
        // Si no se encuentra el paciente
        assertTrue(eventos.get(1).get("title").toString().contains("Paciente #3"));
    }
}