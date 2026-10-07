package gym.ada.api.dto;

import java.math.BigDecimal;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor

public class ProductData {
	
	private Long id;
	
	private String titulo;
	
	private String descripcion;
	
	private String adicional;
	
	private BigDecimal precioUnidad;
	
	private BigDecimal precioTotal;
	
	private Long categoriaId;
	
	private String categoria;
	
	
	public Long getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}

	private boolean activo;
	
	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	private List<TallaDto> tallas;
	
	private List<ImagenDto> imagenes;

	public List<ImagenDto> getImagenes() {
		return imagenes;
	}

	public void setImagenes(List<ImagenDto> imagenes) {
		this.imagenes = imagenes;
	}

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

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public List<TallaDto> getTallas() {
		return tallas;
	}

	public void setTallas(List<TallaDto> tallas) {
		this.tallas = tallas;
	}
	
	
	
}
