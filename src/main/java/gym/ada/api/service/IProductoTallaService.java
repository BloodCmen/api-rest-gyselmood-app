package gym.ada.api.service;

import java.util.List;

import gym.ada.api.model.ProductoTalla;



public interface IProductoTallaService {
	
	public List<ProductoTalla> listarProductoTallas();

	public ProductoTalla buscarProductoTalla(Long id);

	public ProductoTalla guardarProductoTalla(ProductoTalla productoTalla);

	public void eliminarProductoTalla(Long id);

	
}
