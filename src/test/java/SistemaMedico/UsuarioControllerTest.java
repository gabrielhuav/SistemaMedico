package SistemaMedico;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.service.UsuarioService;
import SistemaMedico.controller.UsuarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private CitaRepository citaRepository;
    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contarNotificacionesNoLeidas_debeContarCitasPendientes() {
        // Arrange
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        List<Cita> citas = Arrays.asList(
                citaConEstado("pendiente"),
                citaConEstado("confirmada"),
                citaConEstado("pendiente"),
                citaConEstado("cancelada"));

        when(usuarioService.obtenerUsuarioActual()).thenReturn(usuarioMock);
        when(citaRepository.findByIdPaciente(1L)).thenReturn(citas);

        // Act
        Map<String, Integer> resultado = usuarioController.contarNotificacionesNoLeidas();

        // Assert
        assertEquals(2, resultado.get("nuevas")); // Dos "pendiente"
        verify(usuarioService, times(1)).obtenerUsuarioActual();
        verify(citaRepository, times(1)).findByIdPaciente(1L);
    }

    @Test
    void contarNotificacionesNoLeidas_usuarioNoLogeado() {
        // Arrange
        when(usuarioService.obtenerUsuarioActual()).thenReturn(null);

        // Act
        Map<String, Integer> resultado = usuarioController.contarNotificacionesNoLeidas();

        // Assert
        assertEquals(0, resultado.get("nuevas"));
        verify(usuarioService, times(1)).obtenerUsuarioActual();
        verifyNoInteractions(citaRepository);
    }

    // Helper para crear citas con estado específico
    private Cita citaConEstado(String estado) {
        Cita cita = new Cita();
        cita.setEstado(estado);
        return cita;
    }
}