package gym.ada.api.model;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity  //Para que Spring sepa que esto es un tabla :)
@Table (name = "imagenes")
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {
  
	
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="imagen_id")
	private Long id;
	
	@Column(length = 500, nullable = false)
	@Size(max = 500)
	@NotBlank
	private String url;
	
	@Column(name = "public_id",length = 200, nullable = false)
	@Size(max = 200)
	@NotBlank
	private String publicId;
	
	@Column(name = "es_principal")
	private boolean esPrincipal  = false;

	
	
	
	@ManyToOne
	@JoinColumn (name= "producto_id")
	private Producto producto;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public boolean isEsPrincipal() {
		return esPrincipal;
	}

	public void setEsPrincipal(boolean esPrincipal) {
		this.esPrincipal = esPrincipal;
	}

}
