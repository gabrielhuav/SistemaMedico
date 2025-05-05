package SistemaMedico.controller;

import SistemaMedico.entity.Cita; // Asegúrate de que esta clase exista en el paquete correcto
import SistemaMedico.service.UsuarioService;
import SistemaMedico.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendario")
public class CalendarioController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Método para mostrar la vista del calendario.
     * @return El nombre del archivo HTML del calendario.
     */
    @GetMapping("")
    public String mostrarCalendario() {
        return "calendario"; // Devuelve la vista del calendario
    }

    /**
     * Método para obtener los eventos del calendario en formato JSON.
     * @return Una lista de mapas con los datos de los eventos.
     */
    @GetMapping("/eventos")
    @ResponseBody
    public List<Map<String, Object>> obtenerEventos() {
        var usuarioActual = usuarioService.obtenerUsuarioActual();
        List<Map<String, Object>> eventos = new ArrayList<>();
    
        if (usuarioActual != null) {
            // Obtener las citas del usuario como paciente
            List<Cita> citasPaciente = citaRepository.findByIdPaciente(usuarioActual.getId());
            for (Cita cita : citasPaciente) {
                Map<String, Object> evento = new HashMap<>();
                String nombreDoctor = usuarioService.encontrarPorId(cita.getIdDoctor())
                .map(doctor -> doctor.getNombre())
                .orElse("Doctor #" + cita.getIdDoctor());
                evento.put("title", "Cita con el Dr./Dra. " + nombreDoctor);
                evento.put("start", cita.getFechaHora().toString()); // Fecha de inicio en formato ISO
                evento.put("estado", cita.getEstado()); // Estado de la cita
                eventos.add(evento);
            }
    
            // Obtener las citas del usuario como doctor
            List<Cita> citasDoctor = citaRepository.findByIdDoctor(usuarioActual.getId());
            for (Cita cita : citasDoctor) {
                Map<String, Object> evento = new HashMap<>();
                String nombrePaciente = usuarioService.encontrarPorId(cita.getIdPaciente())
                .map(paciente -> paciente.getNombre())
                .orElse("Paciente #" + cita.getIdPaciente());
                evento.put("title", "Cita con el Paciente " + nombrePaciente);
                evento.put("start", cita.getFechaHora().toString()); // Fecha de inicio en formato ISO
                evento.put("estado", cita.getEstado()); // Estado de la cita
                eventos.add(evento);
            }
        }
    
        System.out.println("Eventos generados: " + eventos); // Depuración
        return eventos;
    }
}