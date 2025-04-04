package SistemaMedico.controller;

import SistemaMedico.entity.Cita;
import SistemaMedico.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;

    // Mostrar la página principal de citas
    @GetMapping
    public String mostrarPaginaCitas(Model model) {
        model.addAttribute("citas", citaRepository.findAll());
        return "citas"; // Nombre del archivo HTML que se mostrará
    }

    // Mostrar el formulario para agendar una nueva cita
    @GetMapping("/agendar")
    public String mostrarFormularioAgendar(Model model) {
        return "agendar"; // Nombre del archivo HTML para el formulario
    }

    // Procesar el formulario para agendar una nueva cita
    @PostMapping("/agendar")
    public String agendarCita(
            @RequestParam("id_paciente") Long idPaciente,
            @RequestParam("id_doctor") Long idDoctor,
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            @RequestParam("motivo") String motivo,
            Model model) {
    
        try {
            // Log para depuración
            System.out.println("Datos recibidos: idPaciente=" + idPaciente + ", idDoctor=" + idDoctor + ", fecha=" + fecha + ", hora=" + hora + ", motivo=" + motivo);
    
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
    
            // Guardar la cita en la base de datos
            citaRepository.save(nuevaCita);
    
            // Log para éxito
            System.out.println("Cita guardada exitosamente.");
            model.addAttribute("mensaje", "Cita agendada exitosamente.");
            return "redirect:/citas";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Hubo un problema al agendar la cita.");
            return "agendar";
        }
    }
    // Eliminar una cita por ID
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

    // Mostrar el formulario para editar una cita
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Cita> cita = citaRepository.findById(id);
        if (cita.isPresent()) {
            model.addAttribute("cita", cita.get());
            return "editar"; // Nombre del archivo HTML para editar
        } else {
            model.addAttribute("error", "Cita no encontrada.");
            return "redirect:/citas";
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
}