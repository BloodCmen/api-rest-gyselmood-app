package gym.ada.api.model;

import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity  //Para que Spring sepa que esto es un tabla :)
@Table (name = "tallas")
@NoArgsConstructor
@AllArgsConstructor
public class Talla {
 
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="talla_id")
	private Long id;
	
	
	@Column(length = 10, nullable = false, unique = true)
	@Size(max = 10)
	@NotBlank
	private String nombre;
	

	@JsonIgnore
	@OneToMany(mappedBy = "talla")
	private List<ProductoTalla> productoTallas;


	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ProductoTalla> getProductoTallas() {
		return productoTallas;
	}

	public void setProductoTallas(List<ProductoTalla> productoTallas) {
		this.productoTallas = productoTallas;
	}


	
	
}
