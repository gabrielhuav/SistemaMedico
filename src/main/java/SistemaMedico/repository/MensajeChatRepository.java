package SistemaMedico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import SistemaMedico.entity.MensajeChat;
import SistemaMedico.entity.Usuario;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Long> {
    
    @Query("SELECT m FROM MensajeChat m WHERE " +
           "(m.emisor.id = :usuarioId AND m.receptor.id = :contactoId) OR " +
           "(m.emisor.id = :contactoId AND m.receptor.id = :usuarioId) " +
           "ORDER BY m.fechaEnvio ASC")
    List<MensajeChat> findByEmisorAndReceptorOrderByFechaEnvioAsc(
        @Param("usuarioId") Long usuarioId, 
        @Param("contactoId") Long contactoId
    );

    // Consulta modificada para obtener contactos
    @Query("SELECT DISTINCT u FROM Usuario u WHERE u.id IN " +
           "(SELECT m.receptor.id FROM MensajeChat m WHERE m.emisor.id = :usuarioId) OR " +
           "u.id IN (SELECT m.emisor.id FROM MensajeChat m WHERE m.receptor.id = :usuarioId)")
    List<Usuario> findContactsByUsuarioId(@Param("usuarioId") Long usuarioId);
}