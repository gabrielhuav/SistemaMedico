package SistemaMedico;

import SistemaMedico.entity.Consulta;
import SistemaMedico.repository.ConsultaRepository;
import SistemaMedico.service.ConsultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private ConsultaService consultaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarConsulta_debeGuardarConsulta() {
        Consulta consulta = new Consulta();
        when(consultaRepository.save(consulta)).thenReturn(consulta);

        Consulta resultado = consultaService.registrarConsulta(consulta);

        assertEquals(consulta, resultado);
        verify(consultaRepository, times(1)).save(consulta);
    }

    @Test
    void obtenerConsultasPorPaciente_debeRetornarConsultas() {
        Long pacienteId = 1L;
        List<Consulta> consultas = Arrays.asList(new Consulta(), new Consulta());
        when(consultaRepository.findByPacienteId(pacienteId)).thenReturn(consultas);

        List<Consulta> resultado = consultaService.obtenerConsultasPorPaciente(pacienteId);

        assertEquals(consultas.size(), resultado.size());
        verify(consultaRepository, times(1)).findByPacienteId(pacienteId);
    }

    @Test
    void obtenerConsultasPorDoctor_debeRetornarConsultas() {
        Long doctorId = 2L;
        List<Consulta> consultas = Arrays.asList(new Consulta());
        when(consultaRepository.findByDoctorId(doctorId)).thenReturn(consultas);

        List<Consulta> resultado = consultaService.obtenerConsultasPorDoctor(doctorId);

        assertEquals(consultas, resultado);
        verify(consultaRepository, times(1)).findByDoctorId(doctorId);
    }

    @Test
    void obtenerTodasLasConsultas_debeRetornarTodas() {
        List<Consulta> consultas = Arrays.asList(new Consulta());
        when(consultaRepository.findAll()).thenReturn(consultas);

        List<Consulta> resultado = consultaService.obtenerTodasLasConsultas();

        assertEquals(consultas, resultado);
        verify(consultaRepository, times(1)).findAll();
    }

    @Test
    void guardarConsulta_debeGuardarConsulta() {
        Consulta consulta = new Consulta();
        consultaService.guardarConsulta(consulta);

        verify(consultaRepository, times(1)).save(consulta);
    }
}