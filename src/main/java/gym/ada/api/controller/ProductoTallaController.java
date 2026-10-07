package gym.ada.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import gym.ada.api.model.ProductoTalla;
import gym.ada.api.service.IProductoTallaService;

@RestController
@RequestMapping("/api/producto-tallas")
public class ProductoTallaController {

    private final IProductoTallaService productoTallaService;

    public ProductoTallaController(IProductoTallaService productoTallaService) {
        this.productoTallaService = productoTallaService;
    }

    // LIST
    @GetMapping("/listProductoTallas")
    public List<ProductoTalla> listarTodos() {
        return productoTallaService.listarProductoTallas();
    }

    // SEARCH
    @GetMapping("/searchProductoTalla/{id}")
    public ProductoTalla buscarPorId(@PathVariable Long id) {
        return productoTallaService.buscarProductoTalla(id);
    }

    // SAVE
    @PostMapping("/saveProductoTalla")
    public ProductoTalla guardar(@RequestBody ProductoTalla productoTalla) {
        return productoTallaService.guardarProductoTalla(productoTalla);
    }

    // DELETE
    @DeleteMapping("/deleteProductoTalla/{id}")
    public void eliminar(@PathVariable Long id) {
        productoTallaService.eliminarProductoTalla(id);
    }
}