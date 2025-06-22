package SistemaMedico.controller;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/ver/{idCita}")
    public String verTicket(@PathVariable Long idCita, Model model) {
        // Buscar la cita por ID
        Optional<Cita> citaOptional = citaRepository.findById(idCita);

        if (citaOptional.isPresent()) {
            Cita cita = citaOptional.get();

            // Obtener información del paciente y doctor
            Usuario paciente = usuarioRepository.findById(cita.getIdPaciente())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
            Usuario doctor = usuarioRepository.findById(cita.getIdDoctor())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

            // Formatear fecha y hora para mejor visualización
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String fechaFormateada = cita.getFechaHora().format(dateFormatter);
            String horaFormateada = cita.getFechaHora().format(timeFormatter);

            // Crear un mapa con la información del ticket
            Map<String, String> ticketInfo = new HashMap<>();
            ticketInfo.put("numeroCita", String.valueOf(cita.getId()));
            ticketInfo.put("fechaCita", fechaFormateada);
            ticketInfo.put("horaCita", horaFormateada);
            ticketInfo.put("pacienteNombre", paciente.getNombre());
            ticketInfo.put("pacienteEmail", paciente.getEmail());
            ticketInfo.put("doctorNombre", doctor.getNombre());
            ticketInfo.put("doctorEmail", doctor.getEmail());
            ticketInfo.put("motivoCita", cita.getMotivo());
            ticketInfo.put("estadoCita", cita.getEstado());

            // Si hay fecha de próxima cita, añadirla al ticket
            if (cita.getFechaProximaCita() != null) {
                ticketInfo.put("fechaProximaCita", cita.getFechaProximaCita().format(dateFormatter));
            } else {
                ticketInfo.put("fechaProximaCita", "No programada");
            }

            model.addAttribute("ticket", ticketInfo);
            return "ticket";
        }

        // Si la cita no existe, redirigir a la lista de citas con un mensaje de error
        model.addAttribute("error", "Cita no encontrada");
        return "redirect:/citas";
    }
}