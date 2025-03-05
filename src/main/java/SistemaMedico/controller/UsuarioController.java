package HolaSpring6CV3.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import HolaSpring6CV3.entity.Usuario;
import HolaSpring6CV3.repository.UsuarioRepository;
import HolaSpring6CV3.service.UsuarioService;;


@Controller
public class UsuarioController {
     @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}