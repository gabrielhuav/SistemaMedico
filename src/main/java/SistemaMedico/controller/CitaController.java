package SistemaMedico.controller;

import SistemaMedico.entity.Cita;
import SistemaMedico.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/citas")
public class CitaController {


    
    @Autowired
    private CitaRepository citaRepository;

    @PostMapping("/agendar")
    public String agendarCita(
            @RequestParam("id_paciente") Long idPaciente,
            @RequestParam("id_doctor") Long idDoctor,
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            @RequestParam("motivo") String motivo,
            Model model) {

        try {
            // Crear una nueva cita
            Cita nuevaCita = new Cita();
            nuevaCita.setIdPaciente(idPaciente);
            nuevaCita.setIdDoctor(idDoctor);
            nuevaCita.setFechaHora(LocalDateTime.parse(fecha + "T" + hora));
            nuevaCita.setMotivo(motivo);
            nuevaCita.setEstado("pendiente");

            // Guardar la cita en la base de datos
            citaRepository.save(nuevaCita);

            // Redirigir al usuario con un mensaje de éxito
            model.addAttribute("mensaje", "Cita agendada exitosamente.");
            return "redirect:/citas/success";
        } catch (Exception e) {
            model.addAttribute("error", "Hubo un problema al agendar la cita.");
            return "redirect:/citas/error";
        }
    }
}