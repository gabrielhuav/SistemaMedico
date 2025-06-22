package SistemaMedico;

import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.UsuarioRepository;
import SistemaMedico.controller.PerfilController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfilControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @InjectMocks
    private PerfilController perfilController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Utilidad para simular autenticación
    private void mockAuthentication(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void actualizarPassword_exito() {
        String username = "user";
        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        usuario.setPassword("hash_actual");

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);
        when(passwordEncoder.matches("actual", "hash_actual")).thenReturn(true);
        when(passwordEncoder.encode("nueva")).thenReturn("hash_nueva");

        mockAuthentication(username);

        String resultado = perfilController.actualizarPassword(
                "nueva", "nueva", "actual", model);

        assertEquals("perfil", resultado);
        verify(usuarioRepository).save(usuario);
        verify(model).addAttribute(eq("mensaje"), contains("éxito"));
        verify(model).addAttribute(eq("tipoMensaje"), eq("exito"));
    }

    @Test
    void actualizarPassword_contraseñaActualIncorrecta() {
        String username = "user";
        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        usuario.setPassword("hash_actual");

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);
        when(passwordEncoder.matches("actual", "hash_actual")).thenReturn(false);

        mockAuthentication(username);

        String resultado = perfilController.actualizarPassword(
                "nueva", "nueva", "actual", model);

        assertEquals("perfil", resultado);
        verify(model).addAttribute(eq("mensaje"), contains("actual es incorrecta"));
        verify(model).addAttribute(eq("tipoMensaje"), eq("error"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizarPassword_nuevasNoCoinciden() {
        String username = "user";
        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        usuario.setPassword("hash_actual");

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);
        when(passwordEncoder.matches("actual", "hash_actual")).thenReturn(true);

        mockAuthentication(username);

        String resultado = perfilController.actualizarPassword(
                "nueva", "diferente", "actual", model);

        assertEquals("perfil", resultado);
        verify(model).addAttribute(eq("mensaje"), contains("no coinciden"));
        verify(model).addAttribute(eq("tipoMensaje"), eq("error"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizarImagen_exito() throws Exception {
        String username = "user";
        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        usuario.setId(1L);

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);

        byte[] imageBytes = new byte[100]; // Simula una imagen pequeña
        MockMultipartFile imagen = new MockMultipartFile("imagen", "test.jpg", "image/jpeg", imageBytes);

        mockAuthentication(username);

        String resultado = perfilController.actualizarImagen(imagen, model);

        assertEquals("perfil", resultado);
        verify(usuarioRepository).save(usuario);
        verify(model).addAttribute(eq("mensaje"), contains("perfil actualizada"));
        verify(model).addAttribute(eq("tipoMensaje"), eq("exito"));
    }

    @Test
    void actualizarImagen_archivoVacio() throws Exception {
        String username = "user";
        Usuario usuario = new Usuario();
        usuario.setNombre(username);

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);

        MockMultipartFile imagen = new MockMultipartFile("imagen", "", "image/jpeg", new byte[0]);
        mockAuthentication(username);

        String resultado = perfilController.actualizarImagen(imagen, model);

        assertEquals("perfil", resultado);
        verify(model).addAttribute(eq("mensaje"), contains("selecciona una imagen"));
        verify(model).addAttribute(eq("tipoMensaje"), eq("error"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizarImagen_tipoNoValido() throws Exception {
        String username = "user";
        Usuario usuario = new Usuario();
        usuario.setNombre(username);

        when(usuarioRepository.findByNombre(username)).thenReturn(usuario);

        MockMultipartFile imagen = new MockMultipartFile("imagen", "file.txt", "text/plain", "contenido".getBytes());
        mockAuthentication(username);

        String resultado = perfilController.actualizarImagen(imagen, model);

        assertEquals("perfil", resultado);
        verify(model).addAttribute(eq("mensaje"), contains("archivo de imagen válido"));
        verify(model).addAttribute(eq("tipoMensaje"), eq("error"));
        verify(usuarioRepository, never()).save(any());
    }
}