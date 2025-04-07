package SistemaMedico.controller;

import SistemaMedico.entity.Consulta;
import SistemaMedico.service.ConsultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

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
        model.addAttribute("idCita", idCita);
        return "agconsulta"; // Nombre del archivo HTML
    }
}