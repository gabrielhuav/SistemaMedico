package SistemaMedico;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.UsuarioRepository;
import SistemaMedico.controller.CitaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CitaControllerTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Model model;

    @InjectMocks
    private CitaController citaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockAuthentication(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void agendarCita_exito() {
        // Arrange
        Long idPaciente = 1L;
        Long idDoctor = 2L;
        String fecha = "2025-07-01";
        String hora = "10:00";
        String motivo = "Consulta general";
        String fechaProximaCita = "2025-08-01";

        when(citaRepository.save(any(Cita.class))).thenReturn(new Cita());

        // Act
        String result = citaController.agendarCita(
                idPaciente, idDoctor, fecha, hora, motivo, fechaProximaCita, model);

        // Assert
        assertEquals("redirect:/citas", result);
        verify(model).addAttribute("mensaje", "Cita agendada exitosamente.");
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    void agendarCita_fallo_camposObligatorios() {
        // Act
        String result = citaController.agendarCita(
                null, null, "", "", "", "", model);

        // Assert
        assertEquals("agendar", result);
        verify(model).addAttribute(eq("error"), contains("Todos los campos son obligatorios."));
        verify(citaRepository, never()).save(any());
    }

    @Test
    void agendarCita_fallo_fechaFormatoInvalido() {
        String result = citaController.agendarCita(
                1L, 2L, "2025-07-01", "invalido", "Motivo", "", model);
        assertEquals("agendar", result);
        verify(model).addAttribute(eq("error"), contains("El formato de fecha u hora es incorrecto."));
        verify(citaRepository, never()).save(any());
    }

    @Test
    void editarCita_exito() {
        Long id = 10L;
        Cita cita = new Cita();
        cita.setId(id);

        when(citaRepository.findById(id)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        String result = citaController.editarCita(
                id, 1L, 2L, "2025-07-01", "10:00", "Motivo", "2025-08-01", model);

        assertEquals("redirect:/citas", result);
        verify(model).addAttribute("mensaje", "Cita actualizada exitosamente.");
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    void editarCita_citaNoEncontrada() {
        Long id = 999L;
        when(citaRepository.findById(id)).thenReturn(Optional.empty());

        String result = citaController.editarCita(
                id, 1L, 2L, "2025-07-01", "10:00", "Motivo", "2025-08-01", model);

        assertEquals("redirect:/citas", result);
        verify(model).addAttribute("error", "Cita no encontrada.");
        verify(citaRepository, never()).save(any());
    }

    @Test
    void cambiarEstadoCita_pendiente_a_confirmada() {
        Long idCita = 5L;
        Cita cita = new Cita();
        cita.setId(idCita);
        cita.setEstado("pendiente");

        when(citaRepository.findById(idCita)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        String result = citaController.cambiarEstadoCita(idCita, model);

        assertEquals("redirect:/citas", result);
        assertEquals("confirmada", cita.getEstado());
        verify(model).addAttribute(eq("mensaje"), contains("cambiado exitosamente"));
        verify(citaRepository).save(cita);
    }

    @Test
    void cambiarEstadoCita_confirmada_a_cancelada() {
        Long idCita = 6L;
        Cita cita = new Cita();
        cita.setId(idCita);
        cita.setEstado("confirmada");

        when(citaRepository.findById(idCita)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        String result = citaController.cambiarEstadoCita(idCita, model);

        assertEquals("redirect:/citas", result);
        assertEquals("cancelada", cita.getEstado());
    }

    @Test
    void cambiarEstadoCita_cancelada_a_pendiente() {
        Long idCita = 7L;
        Cita cita = new Cita();
        cita.setId(idCita);
        cita.setEstado("cancelada");

        when(citaRepository.findById(idCita)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        String result = citaController.cambiarEstadoCita(idCita, model);

        assertEquals("redirect:/citas", result);
        assertEquals("pendiente", cita.getEstado());
    }

    @Test
    void cambiarEstadoCita_citaNoEncontrada() {
        Long idCita = 100L;
        when(citaRepository.findById(idCita)).thenReturn(Optional.empty());

        String result = citaController.cambiarEstadoCita(idCita, model);

        assertEquals("redirect:/citas", result);
        verify(model).addAttribute(eq("error"), contains("Error al cambiar el estado de la cita"));
    }
}