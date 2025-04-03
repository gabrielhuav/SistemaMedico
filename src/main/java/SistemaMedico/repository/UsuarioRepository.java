package SistemaMedico.repository;

import SistemaMedico.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNombre(String nombre);  // EL campo es "nombre"
    public Optional<Usuario> findByEmail(String email);

    
}
