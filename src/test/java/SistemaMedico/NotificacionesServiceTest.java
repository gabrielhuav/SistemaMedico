package SistemaMedico;

import SistemaMedico.entity.Notificacion;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.NotificacionRepository;
import SistemaMedico.service.NotificacionesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificacionesServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionesService notificacionesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerNotificacionesPorUsuario_debeRetornarNotificaciones() {
        Usuario usuario = new Usuario();
        List<Notificacion> notificaciones = Arrays.asList(new Notificacion(), new Notificacion());

        when(notificacionRepository.findByUsuario(usuario)).thenReturn(notificaciones);

        List<Notificacion> resultado = notificacionesService.obtenerNotificacionesPorUsuario(usuario);

        assertEquals(notificaciones, resultado);
        verify(notificacionRepository, times(1)).findByUsuario(usuario);
    }
}