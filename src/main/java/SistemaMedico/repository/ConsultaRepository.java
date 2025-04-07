package SistemaMedico.repository;

import SistemaMedico.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Buscar consultas por el ID del paciente
    List<Consulta> findByPacienteId(Long pacienteId);

    // Buscar consultas por el ID del doctor
    List<Consulta> findByDoctorId(Long doctorId);

    // Buscar consultas programadas para una fecha específica
    List<Consulta> findByFechaProximaCita(LocalDate fechaProximaCita);

    // Consulta personalizada para buscar consultas entre dos fechas
    @Query("SELECT c FROM Consulta c WHERE c.fechaProximaCita BETWEEN :startDate AND :endDate")
    List<Consulta> findConsultasBetweenDates(LocalDate startDate, LocalDate endDate);
}