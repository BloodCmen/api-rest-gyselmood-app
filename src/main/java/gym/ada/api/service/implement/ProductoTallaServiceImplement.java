package gym.ada.api.service.implement;

import java.util.List;

import org.springframework.stereotype.Service;

import gym.ada.api.model.ProductoTalla;
import gym.ada.api.repository.IProductoTallaRepository;
import gym.ada.api.service.IProductoTallaService;

@Service
public class ProductoTallaServiceImplement implements IProductoTallaService {

	private final IProductoTallaRepository productoTallaRepository;

    public ProductoTallaServiceImplement(IProductoTallaRepository productoTallaRepository) {
        this.productoTallaRepository = productoTallaRepository;
    }

    @Override
    public List<ProductoTalla> listarProductoTallas() {
        return productoTallaRepository.findAll();
    }

    @Override
    public ProductoTalla buscarProductoTalla(Long id) {
        return productoTallaRepository.findById(id).orElse(null);
    }

    @Override
    public ProductoTalla guardarProductoTalla(ProductoTalla productoTalla) {
        return productoTallaRepository.save(productoTalla);
    }

    @Override
    public void eliminarProductoTalla(Long id) {
        productoTallaRepository.deleteById(id);
    }

}
