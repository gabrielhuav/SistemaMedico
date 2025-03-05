package HolaSpring6CV3.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import HolaSpring6CV3.entity.Usuario;
import HolaSpring6CV3.repository.UsuarioRepository;
@Controller

public class LoginController {
        @Autowired
    private UsuarioRepository usuarioRepository;
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/home")
   public String home(Model model) {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Buscar usuario en la BD
        Usuario usuario = usuarioRepository.findByNombre(username);
        
        if (usuario != null) {
            model.addAttribute("nombreUsuario", usuario.getNombre());

            // Verificar si el usuario tiene el rol 'ADMIN'
            boolean isAdmin = false;
            for (GrantedAuthority authority : auth.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
            
            // Pasar la variable isAdmin al modelo
            model.addAttribute("isAdmin", isAdmin);

        } else {
            model.addAttribute("nombreUsuario", "Usuario");
        }

        return "home";  // O "index" si es la vista principal
    }

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "accessDenied"; 
    }

    
    
}
