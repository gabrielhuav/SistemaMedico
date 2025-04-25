package SistemaMedico.controller;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Mostrar la página principal de citas
    // Mostrar la página principal de citas
@GetMapping
public String mostrarPaginaCitas(Model model) {
    // Obtener el usuario autenticado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName(); // Suponiendo que el nombre de usuario es el nombre
    System.out.println("Usuario autenticado: " + username);

    // Buscar el usuario autenticado por nombre
    Usuario usuario = usuarioRepository.findByNombre(username);
    if (usuario != null) {
        System.out.println("ID del usuario autenticado: " + usuario.getId());

        // Verificar si el usuario tiene el rol de doctor
        boolean isDoctor = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_DOCTOR"));

        List<Cita> citas;
        if (isDoctor) {
            // Si es doctor, mostrar las citas asignadas a él como doctor
            citas = citaRepository.findByIdDoctor(usuario.getId());
        } else {
            // Si no es doctor, mostrar solo sus citas como paciente
            citas = citaRepository.findByIdPaciente(usuario.getId());
        }

        // Crear un mapa de nombres
        Map<Long, String> nombres = new HashMap<>();
        for (Cita cita : citas) {
            if (isDoctor) {
                nombres.put(cita.getIdPaciente(), usuarioRepository.findById(cita.getIdPaciente()).get().getNombre());
            } else {
                nombres.put(cita.getIdDoctor(), usuarioRepository.findById(cita.getIdDoctor()).get().getNombre());
            }
        }

        model.addAttribute("citas", citas);
        model.addAttribute("nombres", nombres);
        model.addAttribute("isDoctor", isDoctor); // Pasar esta información al modelo si es necesario
    } else {
        System.out.println("Usuario no encontrado.");
        model.addAttribute("citas", List.of()); // Lista vacía si no se encuentra el usuario
    }

    return "citas";
}
    // Mostrar el formulario para agendar una nueva cita
    @GetMapping("/agendar")
    public String mostrarFormularioAgendar(Model model) {
        List<Usuario> doctores = usuarioRepository.findByRolesNombre("ROLE_DOCTOR");
        model.addAttribute("doctores", doctores);
        return "agendar"; // Nombre del archivo HTML para el formulario
    }

    // Procesar el formulario para agendar una nueva cita
   // Procesar el formulario para agendar una nueva cita
@PostMapping("/agendar")
public String agendarCita(
        @RequestParam("id_paciente") Long idPaciente,
        @RequestParam("id_doctor") Long idDoctor,
        @RequestParam("fecha") String fecha,
        @RequestParam("hora") String hora,
        @RequestParam("motivo") String motivo,
        @RequestParam(value = "fecha_proxima_cita", required = false) String fechaProximaCita, // Nueva propiedad
        Model model) {

    try {
        // Validar parámetros
        if (idPaciente == null || idDoctor == null || fecha.isEmpty() || hora.isEmpty() || motivo.isEmpty()) {
            model.addAttribute("error", "Todos los campos son obligatorios.");
            return "agendar";
        }

        // Crear una nueva cita
        Cita nuevaCita = new Cita();
        nuevaCita.setIdPaciente(idPaciente);
        nuevaCita.setIdDoctor(idDoctor);

        // Validar y convertir la fecha y hora
        try {
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora);
            nuevaCita.setFechaHora(fechaHora);
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "El formato de fecha u hora es incorrecto.");
            return "agendar";
        }

        nuevaCita.setMotivo(motivo);
        nuevaCita.setEstado("pendiente");

        // Validar y asignar la fecha de la próxima cita (opcional)
        if (fechaProximaCita != null && !fechaProximaCita.isEmpty()) {
            try {
                nuevaCita.setFechaProximaCita(LocalDate.parse(fechaProximaCita));
            } catch (DateTimeParseException e) {
                model.addAttribute("error", "El formato de la fecha de la próxima cita es incorrecto.");
                return "agendar";
            }
        }

        // Guardar la cita en la base de datos
        citaRepository.save(nuevaCita);

        model.addAttribute("mensaje", "Cita agendada exitosamente.");
        return "redirect:/citas";
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "Hubo un problema al agendar la cita.");
        return "agendar";
    }
}

// Procesar el formulario para editar una cita
@PostMapping("/editar/{id}")
public String editarCita(
        @PathVariable Long id,
        @RequestParam("id_paciente") Long idPaciente,
        @RequestParam("id_doctor") Long idDoctor,
        @RequestParam("fecha") String fecha,
        @RequestParam("hora") String hora,
        @RequestParam("motivo") String motivo,
        @RequestParam(value = "fecha_proxima_cita", required = false) String fechaProximaCita, // Nueva propiedad
        Model model) {

    Optional<Cita> citaOptional = citaRepository.findById(id);
    if (citaOptional.isPresent()) {
        Cita cita = citaOptional.get();

        try {
            // Validar y actualizar los datos
            cita.setIdPaciente(idPaciente);
            cita.setIdDoctor(idDoctor);
            cita.setFechaHora(LocalDateTime.parse(fecha + "T" + hora));
            cita.setMotivo(motivo);

            // Validar y asignar la fecha de la próxima cita (opcional)
            if (fechaProximaCita != null && !fechaProximaCita.isEmpty()) {
                try {
                    cita.setFechaProximaCita(LocalDate.parse(fechaProximaCita));
                } catch (DateTimeParseException e) {
                    model.addAttribute("error", "El formato de la fecha de la próxima cita es incorrecto.");
                    return "editar";
                }
            }

            // Guardar los cambios en la base de datos
            citaRepository.save(cita);

            model.addAttribute("mensaje", "Cita actualizada exitosamente.");
            return "redirect:/citas";
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "El formato de fecha u hora es incorrecto.");
            return "editar";
        }
    } else {
        model.addAttribute("error", "Cita no encontrada.");
        return "redirect:/citas";
    }
}
@GetMapping("/eliminar/{id}")
public String eliminarCita(@PathVariable Long id, Model model) {
    Optional<Cita> cita = citaRepository.findById(id);
    if (cita.isPresent()) {
        citaRepository.deleteById(id);
        model.addAttribute("mensaje", "Cita eliminada exitosamente.");
    } else {
        model.addAttribute("error", "Cita no encontrada.");
    }
    return "redirect:/citas";
}

@GetMapping("/cambiarEstado/{idCita}")
public String cambiarEstadoCita(@PathVariable Long idCita, Model model) {
    try {
        // Buscar la cita por ID
        Cita cita = citaRepository.findById(idCita).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        // Cambiar el estado de la cita
        if ("pendiente".equals(cita.getEstado())) {
            cita.setEstado("confirmada");
        } else if ("confirmada".equals(cita.getEstado())) {
            cita.setEstado("cancelada");
        } else if ("cancelada".equals(cita.getEstado())) {
            cita.setEstado("pendiente");
        }

        // Guardar los cambios
        citaRepository.save(cita);

        model.addAttribute("mensaje", "El estado de la cita se ha cambiado exitosamente.");
    } catch (Exception e) {
        model.addAttribute("error", "Error al cambiar el estado de la cita: " + e.getMessage());
    }

    return "redirect:/citas"; // Redirige a la lista de citas
}

}