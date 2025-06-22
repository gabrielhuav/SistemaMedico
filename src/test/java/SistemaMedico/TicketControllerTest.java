package SistemaMedico;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

// Import the TicketController class
import SistemaMedico.controller.TicketController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verTicket_debeAgregarTicketInfoAlModelo_yRetornarVistaTicket() {
        // Datos de prueba
        Long idCita = 1L;
        Cita cita = new Cita();
        cita.setId(idCita);
        cita.setIdPaciente(10L);
        cita.setIdDoctor(20L);
        cita.setFechaHora(LocalDateTime.of(2025, 6, 22, 10, 30));
        cita.setMotivo("Consulta General");
        cita.setEstado("confirmada");
        cita.setFechaProximaCita(LocalDateTime.of(2025, 7, 1, 11, 0).toLocalDate());

        Usuario paciente = new Usuario();
        paciente.setId(10L);
        paciente.setNombre("Juan Pérez");
        paciente.setEmail("juan@test.com");

        Usuario doctor = new Usuario();
        doctor.setId(20L);
        doctor.setNombre("Dra. Gómez");
        doctor.setEmail("gomez@test.com");

        // Mocks
        when(citaRepository.findById(idCita)).thenReturn(Optional.of(cita));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(doctor));

        // Act
        String viewName = ticketController.verTicket(idCita, model);

        // Assert
        assertEquals("ticket", viewName);
        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("ticket"), captor.capture());

        Map<String, String> ticketInfo = captor.getValue();
        assertEquals("1", ticketInfo.get("numeroCita"));
        assertEquals("Juan Pérez", ticketInfo.get("pacienteNombre"));
        assertEquals("Dra. Gómez", ticketInfo.get("doctorNombre"));
        assertEquals("Consulta General", ticketInfo.get("motivoCita"));
        assertEquals("confirmada", ticketInfo.get("estadoCita"));
        assertEquals("01/07/2025", ticketInfo.get("fechaProximaCita")); // Formato dd/MM/yyyy
        // Puedes agregar más asserts si lo necesitas
    }

    @Test
    void verTicket_citaNoExiste_redirigeConError() {
        Long idCita = 2L;
        when(citaRepository.findById(idCita)).thenReturn(Optional.empty());

        String viewName = ticketController.verTicket(idCita, model);

        assertEquals("redirect:/citas", viewName);
        verify(model).addAttribute(eq("error"), eq("Cita no encontrada"));
    }
}