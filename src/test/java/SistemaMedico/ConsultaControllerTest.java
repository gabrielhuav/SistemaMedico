package SistemaMedico;

import SistemaMedico.entity.Consulta;
import SistemaMedico.entity.Usuario;
import SistemaMedico.entity.Rol;
import SistemaMedico.service.ConsultaService;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.UsuarioRepository;
import SistemaMedico.controller.ConsultaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultaControllerTest {

    @Mock
    private ConsultaService consultaService;

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Model model;

    @InjectMocks
    private ConsultaController consultaController;

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
    void mostrarConsultas_usuarioDoctor() {
        // Arrange
        String username = "doctor";
        mockAuthentication(username);

        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        usuario.setId(1L);

        Rol rolDoctor = new Rol();
        rolDoctor.setNombre("ROLE_DOCTOR");
        usuario.setRoles(Set.of(rolDoctor));

        List<Consulta> consultas = List.of(new Consulta());

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);
        when(consultaService.obtenerConsultasPorDoctor(1L)).thenReturn(consultas);

        // Act
        String result = consultaController.mostrarConsultas(model);

        // Assert
        assertEquals("consultas", result);
        verify(model).addAttribute("consultas", consultas);
        verify(model).addAttribute("isDoctor", true);
    }

    @Test
    void mostrarConsultas_usuarioPaciente() {
        // Arrange
        String username = "paciente";
        mockAuthentication(username);

        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        usuario.setId(2L);

        Rol rolPaciente = new Rol();
        rolPaciente.setNombre("ROLE_PACIENTE");
        usuario.setRoles(Set.of(rolPaciente));

        List<Consulta> consultas = List.of(new Consulta());

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);
        when(consultaService.obtenerConsultasPorPaciente(2L)).thenReturn(consultas);

        // Act
        String result = consultaController.mostrarConsultas(model);

        // Assert
        assertEquals("consultas", result);
        verify(model).addAttribute("consultas", consultas);
        verify(model).addAttribute("isDoctor", false);
    }

    @Test
    void mostrarConsultas_usuarioNoAutenticado() {
        // Arrange
        String username = "desconocido";
        mockAuthentication(username);

        when(usuarioRepository.findByNombre(username)).thenReturn(null);

        // Act
        String result = consultaController.mostrarConsultas(model);

        // Assert
        assertEquals("consultas", result);
        verify(model).addAttribute("consultas", List.of());
    }
}