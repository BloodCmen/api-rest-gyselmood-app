package gym.ada.api.service;

import java.util.List;


import gym.ada.api.dto.ProductCreateDto;
import gym.ada.api.dto.ProductData;
import gym.ada.api.dto.ProductUpdateDto;


public interface IProductoService {

	/* METODOS DE TESTEO BASICO
	public List<Producto> listarProductos(); 
	public Optional<Producto> buscarProductosPorId(Long id);
	public Producto guardarProductos(Producto producto);
	public Producto actualizarProductos(Long id, Producto producto);
	public void eliminarProductos(Long id);
	
	*/
	//Proveniente de dto
	public List<ProductData> obtenerTodosLosProductos();
	
	public List<ProductData> listarProductosActivos();
	public ProductData crearProducto(ProductCreateDto dto);
	
	public ProductData actualizarProductoParcial( Long id, ProductUpdateDto dto );
	
	void desactivarProducto(Long id);
	
	public ProductData activarProducto(Long id);

}