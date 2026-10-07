package gym.ada.api.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {

    private String titulo;

    private String descripcion;

    private String adicional;

    private BigDecimal precioUnidad;

    private BigDecimal precioTotal;

    private Long categoriaId;

    private List<ProductoTallaDto> tallas;

    private List<ImagenCreateDto> imagenes;

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

	public Long getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}

	public List<ProductoTallaDto> getTallas() {
		return tallas;
	}

	public void setTallas(List<ProductoTallaDto> tallas) {
		this.tallas = tallas;
	}

	public List<ImagenCreateDto> getImagenes() {
		return imagenes;
	}

	public void setImagenes(List<ImagenCreateDto> imagenes) {
		this.imagenes = imagenes;
	}
    
    

}