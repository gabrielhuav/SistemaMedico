package SistemaMedico.repository;

import SistemaMedico.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNombre(String nombre);  // EL campo es "nombre"
     Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.nombre = :rolNombre")
    List<Usuario> findByRolesNombre(@Param("rolNombre") String rolNombre);
    
    @Query("SELECT u.nombre FROM Usuario u WHERE u.id = :id")
String findNombreById(@Param("id") Long id);
}
