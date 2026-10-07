package gym.ada.api.model;

import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "productos")
@NoArgsConstructor  // ← constructor vacío que necesita JPA
@AllArgsConstructor // ← constructor con todos los campos
public class Producto {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="producto_id")
	private Long id;
	
	@Column(length = 200, nullable = false)
	@Size(max = 200)
	@NotBlank
	private String titulo;
	
	@Column(length = 500, nullable = true)
	@Size(max = 500)
	private String descripcion;
	
	@Column(length = 100, nullable = true)
	@Size(max = 100)
	
	private String adicional;
	
	@Column(name = "precio_unidad" , precision = 10 , scale = 2  , nullable = false)
	@Digits(integer = 8, fraction = 2)
	@NotNull
	private BigDecimal precioUnidad;
	
	@Column(name = "precio_total" , precision = 10 , scale = 2  , nullable = true)
	@Digits(integer = 8, fraction = 2)
	
	private BigDecimal precioTotal;
	
	
	private boolean activo = true;
	
	@ManyToOne
	@JoinColumn (name= "categoria_id")
	private Categoria categoria;
	
	
	@OneToMany(
	        mappedBy = "producto",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	)
	@JsonIgnore
	private Set<Imagen> imagenes = new HashSet<>();


	@OneToMany(
	        mappedBy = "producto",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	)
	@JsonIgnore
	private Set<ProductoTalla> productoTallas = new HashSet<>();


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTitulo() {
		return titulo;
	}


	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public String getAdicional() {
		return adicional;
	}


	public void setAdicional(String adicional) {
		this.adicional = adicional;
	}


	public BigDecimal getPrecioUnidad() {
		return precioUnidad;
	}


	public void setPrecioUnidad(BigDecimal precioUnidad) {
		this.precioUnidad = precioUnidad;
	}


	public BigDecimal getPrecioTotal() {
		return precioTotal;
	}


	public void setPrecioTotal(BigDecimal precioTotal) {
		this.precioTotal = precioTotal;
	}


	public boolean isActivo() {
		return activo;
	}


	public void setActivo(boolean activo) {
		this.activo = activo;
	}


	public Categoria getCategoria() {
		return categoria;
	}


	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}


	public Set<Imagen> getImagenes() {
		return imagenes;
	}


	public void setImagenes(Set<Imagen> imagenes) {
		this.imagenes = imagenes;
	}


	public Set<ProductoTalla> getProductoTallas() {
		return productoTallas;
	}


	public void setProductoTallas(Set<ProductoTalla> productoTallas) {
		this.productoTallas = productoTallas;
	}




	
	
	//constructor

	
	
	
}
