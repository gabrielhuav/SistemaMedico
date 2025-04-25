package SistemaMedico.controller;

import SistemaMedico.entity.Cita;
import SistemaMedico.entity.Consulta;
import SistemaMedico.entity.Usuario;
import SistemaMedico.service.ConsultaService;
import SistemaMedico.repository.CitaRepository;
import SistemaMedico.repository.UsuarioRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
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
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;

    public ConsultaController(ConsultaService consultaService, CitaRepository citaRepository, UsuarioRepository usuarioRepository) {
        this.consultaService = consultaService;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository; // Inyecta el repositorio
    }
   @GetMapping
    public String mostrarConsultas(Model model) {
    // Obtener el usuario autenticado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    // Buscar el usuario autenticado por nombre
    Usuario usuario = usuarioRepository.findByNombre(username);
    if (usuario != null) {
        // Verificar si el usuario tiene el rol de doctor
        boolean isDoctor = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_DOCTOR"));

        List<Consulta> consultas;
        if (isDoctor) {
            // Si es doctor, mostrar las consultas asignadas a él como doctor
            consultas = consultaService.obtenerConsultasPorDoctor(usuario.getId());
        } else {
            // Si no es doctor, mostrar solo sus consultas como paciente
            consultas = consultaService.obtenerConsultasPorPaciente(usuario.getId());
        }

        model.addAttribute("consultas", consultas);
        model.addAttribute("isDoctor", isDoctor); // Pasar el rol al modelo si es necesario
    } else {
        model.addAttribute("consultas", List.of()); // Si no hay usuario autenticado, lista vacía
    }

    return "consultas"; // Nombre del archivo HTML
}
    @GetMapping("/agregar/{idCita}")
    public String mostrarFormularioAgregarConsulta(@PathVariable Long idCita, Model model) {
        // Obtener la cita por ID
        Cita cita = citaRepository.findById(idCita).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
    
        // Obtener el paciente y el doctor asociados a la cita
        Usuario paciente = usuarioRepository.findById(cita.getIdPaciente()).orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        Usuario doctor = usuarioRepository.findById(cita.getIdDoctor()).orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));
    
        // Pasar los datos al modelo
        model.addAttribute("idCita", idCita);
        model.addAttribute("idPaciente", paciente.getId());
        model.addAttribute("idDoctor", doctor.getId());
    
        return "agconsulta"; // Nombre del archivo HTML
    }
    
  @PostMapping("/guardar")
    public String guardarConsulta(
            @RequestParam Long idCita,
            @RequestParam Long idPaciente,
            @RequestParam Long idDoctor,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam String sintomas,
            @RequestParam(required = false) String medicamentos,
            @RequestParam(required = false) String dosis,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaProximaCita,
            @RequestParam Consulta.EstadoConsulta estado, 
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
            consulta.setEstado(estado);
            consulta.setFechaProximaCita(fechaProximaCita);

            // Guardar la consulta
            consultaService.guardarConsulta(consulta);

            Cita cita = citaRepository.findById(idCita).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
            cita.setEstado("confirmada"); // Cambia el estado de la cita a "confirmada"
            citaRepository.save(cita);

            

            model.addAttribute("mensaje", "Consulta registrada exitosamente.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar la consulta: " + e.getMessage());
        }
        return "redirect:/consultas"; // Redirige a la lista de consultas
    }
    
}