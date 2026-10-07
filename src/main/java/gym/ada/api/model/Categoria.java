package gym.ada.api.model;

import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "categorias")
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
   
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "categoria_id")
	private Long id;
	
	
	@Column(length = 10, nullable = false, unique = true)
	@Size(max = 10)
	@NotBlank
	private String codigo;
	
	
	@Column(length = 100, nullable = false)
	@Size(max = 100)
	@NotBlank
	private String nombre;
	
	@OneToMany(mappedBy = "categoria")
	@JsonIgnore //Evita bucle infinito y erroes como serializar otra entidad o tabla
	private List<Producto> productos;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	}
