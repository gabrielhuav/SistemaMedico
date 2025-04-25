package SistemaMedico.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.UsuarioRepository;
import SistemaMedico.service.NotificacionesService;
import SistemaMedico.service.UsuarioService;
import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Notificacion; // Ensure this import matches the package where Notificacion is defined
import SistemaMedico.repository.CitaRepository; // Import the CitaRepository


@Controller
public class UsuarioController {
     @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificacionesService servicioNotificaciones;
    
    @Autowired
    private CitaRepository citaRepository; // Declare and autowire CitaRepository

    // Mostrar todos los usuarios
    // Mostrar todos los usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";  // Vista que lista a todos los usuarios
    }

    // Crear nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "crearUsuario";  // Vista con formulario para crear un nuevo usuario
    }

    @PostMapping("/usuarios")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";  // Redirige a la lista de usuarios
    }

    // Ver un usuario por ID
    @GetMapping("/usuarios/{id}")
    public String verUsuario(@PathVariable("id") Long id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "verUsuario";  // Vista para ver un usuario específico
        } else {
            return "redirect:/admin/usuarios";
        }
    }

    // Actualizar un usuario
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "editarUsuario";  // Vista para editar un usuario
        } else {
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/usuarios/editar/{id}")
    public String actualizarUsuario(@PathVariable("id") Long id, @ModelAttribute Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuarioGuardado = usuarioExistente.get();
            usuarioGuardado.setNombre(usuario.getNombre());
            usuarioGuardado.setEmail(usuario.getEmail());
            usuarioGuardado.setPassword(passwordEncoder.encode(usuario.getPassword()));  // Encriptar contraseña
            usuarioRepository.save(usuarioGuardado);
        }
        return "redirect:/admin/usuarios";  // Redirige a la lista de usuarios
    }

    // Eliminar un usuario
   
    public String eliminarUsuario(@PathVariable("id") Long id, Model model) {
        // Elimina al usuario usando el servicio
        usuarioService.eliminarUsuario(id);

        // Añadir un mensaje de éxito para mostrar al usuario
        model.addAttribute("mensaje", "Usuario eliminado exitosamente");

        // Redirigir a la lista de usuarios o a otra página según lo que desees
        return "redirect:/usuarios";  // Redirige a la lista de usuarios
    }

    // @GetMapping("/usuarios/eliminar/{id}")
    // public String eliminarUsuario(@PathVariable("id") Long id, Model model) {
    //     // Elimina al usuario usando el servicio
    //     usuarioService.eliminarUsuario(id);

    //     // Añadir un mensaje de éxito para mostrar al usuario
    //     model.addAttribute("mensaje", "Usuario eliminado exitosamente");

    //     // Redirigir a la lista de usuarios o a otra página según lo que desees
    //     return "redirect:/usuarios";  // Redirige a la lista de usuarios
    // }
    
    // @GetMapping("/usuarios/eliminar")
    // public String eliminarUsuario(@PathVariable("id") Long id) {
    //     usuarioRepository.deleteById(id);
    //     return "redirect:/usuarios";  // Redirige a la lista de usuarios
    // }

    
    @PostMapping("/user/register")
    public String registrarUsuario(@ModelAttribute Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }

    @GetMapping("/notificaciones")
    public String mostrarNotificaciones(Model model) {
        Usuario usuarioActual = usuarioService.obtenerUsuarioActual();
        if (usuarioActual != null) {
            System.out.println("Usuario actual: " + usuarioActual.getNombre());
    
            // Obtener citas del usuario actual
            List<Cita> citas = citaRepository.findByIdPaciente(usuarioActual.getId());
            System.out.println("Citas encontradas: " + citas.size());
    
            List<Map<String, String>> notificaciones = new ArrayList<>();
            for (Cita cita : citas) {
                // Buscar al doctor por su ID
                Usuario doctor = usuarioRepository.findById(cita.getIdDoctor()).orElse(null);
                String nombreDoctor = (doctor != null) ? doctor.getNombre() : "Desconocido";
    
                Map<String, String> notificacion = new HashMap<>();
                notificacion.put("fecha", cita.getFechaHora().toString());
                notificacion.put("titulo", "Cita programada");
                notificacion.put("descripcion", "Tienes una cita con el Dr./Dra. " + nombreDoctor );//+ " el " + cita.getFechaHora());
                notificaciones.add(notificacion);

                if ("confirmada".equals(cita.getEstado())) {
                    Map<String, String> notificacionConcluida = new HashMap<>();
                    notificacionConcluida.put("fecha", cita.getFechaHora().toString());
                    notificacionConcluida.put("titulo", "CITA CONCLUIDA");
                    notificacionConcluida.put("descripcion", "Tu cita con el Dr./Dra. " + nombreDoctor + " ha concluido.");
                    notificaciones.add(notificacionConcluida);
                }
                if ("cancelada".equals(cita.getEstado())) {
                    Map<String, String> notificacionConcluida = new HashMap<>();
                    notificacionConcluida.put("fecha", cita.getFechaHora().toString());
                    notificacionConcluida.put("titulo", "CITA CANCELADA");
                    notificacionConcluida.put("descripcion", "Tu cita con el Dr./Dra. " + nombreDoctor + " ha sido cancelada.");
                    notificaciones.add(notificacionConcluida);
                }
            }
    
            model.addAttribute("notificaciones", notificaciones);
        } else {
            System.out.println("Usuario no autenticado o no encontrado.");
            model.addAttribute("notificaciones", List.of());
        }
        return "notificaciones";
    }
}