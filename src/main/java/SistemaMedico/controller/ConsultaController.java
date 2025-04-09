package SistemaMedico.controller;

import SistemaMedico.entity.Consulta;
import SistemaMedico.entity.Usuario;
import SistemaMedico.service.ConsultaService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public String mostrarConsultas(Model model) {
        List<Consulta> consultas = consultaService.obtenerTodasLasConsultas();
        model.addAttribute("consultas", consultas);
        return "consultas"; // Nombre del archivo HTML en templates
    }
    @GetMapping("/agregar/{idCita}")
    public String mostrarFormularioAgregarConsulta(@PathVariable Long idCita, Model model) {
        // Simula obtener los IDs del paciente y doctor asociados a la cita
        Long idPaciente = 1L; // Reemplaza con lógica real para obtener el ID del paciente
        Long idDoctor = 2L;  // Reemplaza con lógica real para obtener el ID del doctor
    
        model.addAttribute("idCita", idCita);
        model.addAttribute("idPaciente", idPaciente);
        model.addAttribute("idDoctor", idDoctor);
        return "agconsulta"; // Nombre del archivo HTML
    }
    
  @PostMapping("/guardar")
    public String guardarConsulta(
            @RequestParam Long idPaciente,
            @RequestParam Long idDoctor,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam String sintomas,
            @RequestParam(required = false) String medicamentos,
            @RequestParam(required = false) String dosis,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaProximaCita,
            Model model) {
        try {
            // Crear una nueva consulta
            Consulta consulta = new Consulta();
            consulta.setPaciente(new Usuario(idPaciente)); // Asume que Usuario tiene un constructor con ID
            consulta.setDoctor(new Usuario(idDoctor));
            consulta.setFechaHora(fechaHora);
            consulta.setSintomas(sintomas);
            consulta.setMedicamentos(medicamentos);
            consulta.setDosis(dosis);
            consulta.setEstado(Consulta.EstadoConsulta.pendiente);
            consulta.setFechaProximaCita(fechaProximaCita);

            // Guardar la consulta
            consultaService.guardarConsulta(consulta);

            model.addAttribute("mensaje", "Consulta registrada exitosamente.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar la consulta: " + e.getMessage());
        }
        return "redirect:/consultas"; // Redirige a la lista de consultas
    }
    
}