package gym.ada.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comentario_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private String contenido;

    private Integer estrellas;

    private LocalDateTime fecha = LocalDateTime.now();

    private Boolean activo = true;
    
    @Column(name = "respuesta_admin")
    private String respuestaAdmin;
    
    
    
    public String getRespuestaAdmin() { 
    	return respuestaAdmin; 
    }
    
    public void setRespuestaAdmin(String respuestaAdmin) { 
    	this.respuestaAdmin = respuestaAdmin; 
    }

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public Integer getEstrellas() {
		return estrellas;
	}
	public void setEstrellas(Integer estrellas) {
		this.estrellas = estrellas;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	

    
    
    
}