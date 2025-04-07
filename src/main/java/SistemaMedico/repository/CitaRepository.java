package SistemaMedico.repository;

import SistemaMedico.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Buscar citas por fecha de la próxima cita
    List<Cita> findByFechaProximaCita(LocalDate fechaProximaCita);
    List<Cita> findByIdPaciente(Long idPaciente);
    List<Cita> findByIdDoctor(Long idDoctor);
}