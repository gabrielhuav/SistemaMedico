package SistemaMedico.service;

import SistemaMedico.entity.Consulta;
import SistemaMedico.repository.ConsultaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;

    public ConsultaService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public Consulta registrarConsulta(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    public List<Consulta> obtenerConsultasPorPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteId(pacienteId);
    }
    public List<Consulta> obtenerConsultasPorDoctor(Long doctorId) {
        return consultaRepository.findByDoctorId(doctorId);
    }
    public List<Consulta> obtenerTodasLasConsultas() {
        return consultaRepository.findAll();
    }
    
    public void guardarConsulta(Consulta consulta) {
        consultaRepository.save(consulta);
    }
}