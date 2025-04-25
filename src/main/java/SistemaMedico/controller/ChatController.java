package SistemaMedico.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import SistemaMedico.entity.MensajeChat;
import SistemaMedico.entity.Usuario;
import SistemaMedico.repository.MensajeChatRepository;
import SistemaMedico.repository.UsuarioRepository;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MensajeChatRepository mensajeChatRepository;

    @GetMapping
    public String mostrarChat(@RequestParam(required = false) Long contactId, Model model) {
    // Obtener usuario actual
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario usuarioActual = usuarioRepository.findByNombre(auth.getName());

    // Obtener todos los posibles contactos según el rol
    List<Usuario> allPossibleContacts = new ArrayList<>();
    if (usuarioActual.getRoles().stream().anyMatch(r -> r.getNombre().equals("ROLE_DOCTOR"))) {
        allPossibleContacts = usuarioRepository.findByRolesNombre("ROLE_USER");
    } else {
        allPossibleContacts = usuarioRepository.findByRolesNombre("ROLE_DOCTOR");
    }

    // Obtener contactos con historial de chat
    List<Usuario> contactsWithChat = new ArrayList<>();
    try {
        contactsWithChat = mensajeChatRepository.findContactsByUsuarioId(usuarioActual.getId());
    } catch (Exception e) {
        // Log error but continue
        System.err.println("Error al obtener contactos del chat: " + e.getMessage());
    }

    // Combinar y eliminar duplicados
    Set<Usuario> uniqueContacts = new HashSet<>(allPossibleContacts);
    uniqueContacts.addAll(contactsWithChat);
    List<Usuario> contacts = new ArrayList<>(uniqueContacts);

    model.addAttribute("contacts", contacts);
    model.addAttribute("currentUserId", usuarioActual.getId());
    model.addAttribute("currentUser", usuarioActual);

    if (contactId != null) {
        Usuario contacto = usuarioRepository.findById(contactId).orElse(null);
        if (contacto != null) {
            model.addAttribute("selectedContact", contacto);
            model.addAttribute("selectedContactId", contactId);

            // Obtener mensajes en ambas direcciones
            List<MensajeChat> mensajes = mensajeChatRepository.findByEmisorAndReceptorOrderByFechaEnvioAsc(
                usuarioActual.getId(), contactId);
            model.addAttribute("messages", mensajes);
        }
    }

    return "chat";
    }

    @PostMapping("/enviar")
    @ResponseBody
    public Map<String, Object> enviarMensaje(
            @RequestParam Long receptorId,
            @RequestParam String mensaje) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario emisor = usuarioRepository.findByNombre(auth.getName());
            Usuario receptor = usuarioRepository.findById(receptorId)
                .orElseThrow(() -> new RuntimeException("Receptor no encontrado"));

            MensajeChat nuevoMensaje = new MensajeChat();
            nuevoMensaje.setEmisor(emisor);
            nuevoMensaje.setReceptor(receptor);
            nuevoMensaje.setMensaje(mensaje);
            nuevoMensaje.setFechaEnvio(LocalDateTime.now());
            nuevoMensaje.setLeido(false);

            mensajeChatRepository.save(nuevoMensaje);

            return Map.of(
                "success", true,
                "message", "Mensaje enviado correctamente",
                "timestamp", nuevoMensaje.getFechaEnvio().toString(),
                "messageId", nuevoMensaje.getId()
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error al enviar mensaje: " + e.getMessage()
            );
        }
    }

    @GetMapping("/mensajes/{contactId}")
    @ResponseBody
    public List<MensajeChat> obtenerMensajes(@PathVariable Long contactId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioRepository.findByNombre(auth.getName());

        List<MensajeChat> mensajes = mensajeChatRepository.findByEmisorAndReceptorOrderByFechaEnvioAsc(
            usuarioActual.getId(), contactId);

        // Marcar mensajes como leídos
        mensajes.stream()
            .filter(m -> m.getReceptor().getId().equals(usuarioActual.getId()) && !m.isLeido())
            .forEach(m -> {
                m.setLeido(true);
                mensajeChatRepository.save(m);
            });

        return mensajes;
    }

    @GetMapping("/mensajes/no-leidos/{contactId}")
    @ResponseBody
    public long obtenerCantidadMensajesNoLeidos(@PathVariable Long contactId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioRepository.findByNombre(auth.getName());

        return mensajeChatRepository.findByEmisorAndReceptorOrderByFechaEnvioAsc(usuarioActual.getId(), contactId)
            .stream()
            .filter(m -> m.getReceptor().getId().equals(usuarioActual.getId()) && !m.isLeido())
            .count();
    }
}