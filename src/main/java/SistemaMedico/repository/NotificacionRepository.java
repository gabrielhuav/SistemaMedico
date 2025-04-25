package SistemaMedico.repository;

import SistemaMedico.entity.Notificacion;
import SistemaMedico.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuario(Usuario usuario);
}