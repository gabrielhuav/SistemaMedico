package SistemaMedico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import SistemaMedico.entity.Rol;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.RolRepository;
import SistemaMedico.repository.UsuarioRepository;

@Controller
public class AdminController {
    private final String API_URL = "http://localhost:8086/api";  // URL de la API (ajusta según tu configuración)

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UsuarioRepository userRepository;

    @Autowired
    private RolRepository roleRepository;

    @GetMapping("/admin")
    public String adminPage(Authentication authentication, Model model) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            ResponseEntity<Usuario[]> response = restTemplate.getForEntity(API_URL + "/users", Usuario[].class);
            model.addAttribute("usuarios", response.getBody());
            return "admin";  // Vista para administrador
        } else {
            return "accessDenied";  // Vista si no es admin
        }
    }

    @PostMapping("/admin/users")
    public String addUser(@ModelAttribute Usuario usuario) {
        restTemplate.postForObject(API_URL + "/register", usuario, String.class);
        return "redirect:/admin";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        restTemplate.delete(API_URL + "/users/" + id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/users/{id}/toggle-admin")
    public String toggleAdmin(@PathVariable Long id) {
        Usuario usuario = userRepository.findById(id).orElse(null);
        if (usuario != null) {
            if (usuario.getRoles().stream().anyMatch(rol -> rol.getNombre().equals("ROLE_ADMIN"))) {
                usuario.getRoles().removeIf(rol -> rol.getNombre().equals("ROLE_ADMIN"));
            } else {
                Rol adminRole = roleRepository.findByNombre("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                usuario.getRoles().add(adminRole);
            }
            userRepository.save(usuario);
        }
        return "redirect:/admin";
    }
}