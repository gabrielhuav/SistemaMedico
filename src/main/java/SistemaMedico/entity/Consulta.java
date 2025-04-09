package SistemaMedico.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Usuario paciente;

    @ManyToOne
    @JoinColumn(name = "id_doctor", nullable = false)
    private Usuario doctor;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora; // Refleja fecha y hora de la consulta

    @Column(name = "sintomas", columnDefinition = "TEXT")
    private String sintomas; // Descripción de los síntomas del paciente

    @Column(name = "medicamentos", columnDefinition = "TEXT")
    private String medicamentos; // Medicamentos recetados

    @Column(name = "dosis", columnDefinition = "TEXT")
    private String dosis; // Dosis de los medicamentos

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoConsulta estado; // Estado de la consulta (Pendiente, Confirmada, Cancelada)

    @Column(name = "fecha_proxima_cita")
    private LocalDate fechaProximaCita; // Fecha de la próxima cita, si aplica

    // Enum para el estado de la consulta
    public enum EstadoConsulta {
        pendiente,
        confirmada,
        cancelada
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }

    public Usuario getDoctor() {
        return doctor;
    }

    public void setDoctor(Usuario doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public EstadoConsulta getEstado() {
        return estado;
    }

    public void setEstado(EstadoConsulta estado) {
        this.estado = estado;
    }

    public LocalDate getFechaProximaCita() {
        return fechaProximaCita;
    }

    public void setFechaProximaCita(LocalDate fechaProximaCita) {
        this.fechaProximaCita = fechaProximaCita;
    }
}