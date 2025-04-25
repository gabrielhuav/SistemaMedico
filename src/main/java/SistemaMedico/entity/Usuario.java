package SistemaMedico.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    private byte[] imagen;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "emisor", cascade = CascadeType.ALL)
    private List<MensajeChat> mensajesEnviados = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receptor", cascade = CascadeType.ALL)
    private List<MensajeChat> mensajesRecibidos = new ArrayList<>();

    // Constructores
    public Usuario() {
    }

    public Usuario(Long id) {
        this.id = id;
    }

    // Getters y Setters existentes...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }
    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public List<MensajeChat> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(List<MensajeChat> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

    public List<MensajeChat> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(List<MensajeChat> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }
}