package SistemaMedico.service;

import SistemaMedico.entity.Notificacion;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionesService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<Notificacion> obtenerNotificacionesPorUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuario(usuario);
    }
}