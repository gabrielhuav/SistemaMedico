package SistemaMedico;

import SistemaMedico.entity.MensajeChat;
import SistemaMedico.entity.Usuario;
import SistemaMedico.entity.Rol;
import SistemaMedico.repository.MensajeChatRepository;
import SistemaMedico.repository.UsuarioRepository;
import SistemaMedico.controller.ChatController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MensajeChatRepository mensajeChatRepository;

    @Mock
    private Model model;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockAuthentication(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Usuario createUser(Long id, String nombre, String rolName) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        Rol rol = new Rol();
        rol.setNombre(rolName);
        usuario.setRoles(Set.of(rol));
        return usuario;
    }

    @Test
    void mostrarChat_paraDoctor() {
        Usuario doctor = createUser(1L, "doctor", "ROLE_DOCTOR");
        Usuario paciente1 = createUser(2L, "user1", "ROLE_USER");
        Usuario paciente2 = createUser(3L, "user2", "ROLE_USER");
        List<Usuario> pacientes = Arrays.asList(paciente1, paciente2);

        mockAuthentication("doctor");
        when(usuarioRepository.findByNombre("doctor")).thenReturn(doctor);
        when(usuarioRepository.findByRolesNombre("ROLE_USER")).thenReturn(pacientes);
        when(mensajeChatRepository.findContactsByUsuarioId(1L)).thenReturn(Collections.emptyList());

        String result = chatController.mostrarChat(null, model);

        assertEquals("chat", result);
        verify(model).addAttribute(eq("contacts"), any(List.class));
        verify(model).addAttribute("currentUserId", 1L);
        verify(model).addAttribute("currentUser", doctor);
    }

    @Test
    void mostrarChat_paraPaciente_conMensajesPrevios() {
        Usuario paciente = createUser(2L, "user1", "ROLE_USER");
        Usuario doctor = createUser(1L, "doctor", "ROLE_DOCTOR");
        List<Usuario> doctores = List.of(doctor);
        List<Usuario> contactsWithChat = List.of(doctor);

        mockAuthentication("user1");
        when(usuarioRepository.findByNombre("user1")).thenReturn(paciente);
        when(usuarioRepository.findByRolesNombre("ROLE_DOCTOR")).thenReturn(doctores);
        when(mensajeChatRepository.findContactsByUsuarioId(2L)).thenReturn(contactsWithChat);

        String result = chatController.mostrarChat(null, model);

        assertEquals("chat", result);
        verify(model).addAttribute(eq("contacts"), argThat(list -> ((List<Usuario>) list).contains(doctor)));
        verify(model).addAttribute("currentUserId", 2L);
        verify(model).addAttribute("currentUser", paciente);
    }

    @Test
    void enviarMensaje_exito() {
        Usuario emisor = createUser(1L, "doctor", "ROLE_DOCTOR");
        Usuario receptor = createUser(2L, "user1", "ROLE_USER");
        MensajeChat mensaje = new MensajeChat();
        mensaje.setId(100L);

        mockAuthentication("doctor");
        when(usuarioRepository.findByNombre("doctor")).thenReturn(emisor);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(receptor));
        when(mensajeChatRepository.save(any(MensajeChat.class))).then(inv -> {
            MensajeChat saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        Map<String, Object> result = chatController.enviarMensaje(2L, "Hola!");

        assertTrue((Boolean) result.get("success"));
        assertEquals("Mensaje enviado correctamente", result.get("message"));
        assertNotNull(result.get("timestamp"));
        assertEquals(100L, result.get("messageId"));
    }

    @Test
    void enviarMensaje_fallo_receptorNoExiste() {
        Usuario emisor = createUser(1L, "doctor", "ROLE_DOCTOR");

        mockAuthentication("doctor");
        when(usuarioRepository.findByNombre("doctor")).thenReturn(emisor);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        Map<String, Object> result = chatController.enviarMensaje(2L, "Hola!");

        assertFalse((Boolean) result.get("success"));
        assertTrue(result.get("message").toString().contains("Receptor no encontrado"));
    }

    @Test
    void obtenerMensajes_marcaComoLeidos() {
        Usuario usuario = createUser(1L, "doctor", "ROLE_DOCTOR");
        Usuario contacto = createUser(2L, "user1", "ROLE_USER");

        MensajeChat noLeido = new MensajeChat();
        noLeido.setId(10L);
        noLeido.setEmisor(contacto);
        noLeido.setReceptor(usuario);
        noLeido.setLeido(false);

        MensajeChat leido = new MensajeChat();
        leido.setId(11L);
        leido.setEmisor(usuario);
        leido.setReceptor(contacto);
        leido.setLeido(true);

        List<MensajeChat> mensajes = Arrays.asList(noLeido, leido);

        mockAuthentication("doctor");
        when(usuarioRepository.findByNombre("doctor")).thenReturn(usuario);
        when(mensajeChatRepository.findByEmisorAndReceptorOrderByFechaEnvioAsc(1L, 2L)).thenReturn(mensajes);

        List<MensajeChat> result = chatController.obtenerMensajes(2L);

        assertEquals(2, result.size());
        assertTrue(noLeido.isLeido());
        verify(mensajeChatRepository, atLeastOnce()).save(noLeido);
    }

    @Test
    void obtenerCantidadMensajesNoLeidos_funciona() {
        Usuario usuario = createUser(1L, "doctor", "ROLE_DOCTOR");
        Usuario contacto = createUser(2L, "user1", "ROLE_USER");

        MensajeChat noLeido = new MensajeChat();
        noLeido.setId(10L);
        noLeido.setEmisor(contacto);
        noLeido.setReceptor(usuario);
        noLeido.setLeido(false);

        MensajeChat leido = new MensajeChat();
        leido.setId(11L);
        leido.setEmisor(usuario);
        leido.setReceptor(contacto);
        leido.setLeido(true);

        List<MensajeChat> mensajes = Arrays.asList(noLeido, leido);

        mockAuthentication("doctor");
        when(usuarioRepository.findByNombre("doctor")).thenReturn(usuario);
        when(mensajeChatRepository.findByEmisorAndReceptorOrderByFechaEnvioAsc(1L, 2L)).thenReturn(mensajes);

        long count = chatController.obtenerCantidadMensajesNoLeidos(2L);

        assertEquals(1, count);
    }
}