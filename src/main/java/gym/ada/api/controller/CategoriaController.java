package gym.ada.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import gym.ada.api.dto.CategoriaCreateDto;
import gym.ada.api.dto.CategoriaDto;
import gym.ada.api.model.Categoria;
import gym.ada.api.service.ICategoriaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;




@RestController // responde con JSON automaticamente
@RequestMapping("/api/categorias")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoriaController {
	
	private ICategoriaService categoriaService;

    public CategoriaController(ICategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
    
    
	 // GET http://localhost:8080/api/categorias
    @GetMapping("/listCategorias")
    public List<Categoria> listarTodos() {
        return categoriaService.listarCategoria();
    }
    
    
    @PostMapping("/saveCategoria")
    public Categoria guardar(@RequestBody Categoria categoria) {
        return categoriaService.guardarCategoria(categoria);
    }
    
 // GET http://localhost:8080/api/categorias/1
    @GetMapping("/searchCategoria/{id}")
    public Categoria buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarCategoriaPorId(id);
    }
    
   // DELETE http://localhost:8080/api/categorias/1
    @DeleteMapping("/deleteCategoria/{id}")
    public void eliminar(@PathVariable Long id) {
    	categoriaService.eliminarCategoria(id);
    }
    
    

    // =========================================================
    // LISTAR CATEGORÍAS
    // GET /api/categorias/listarCategorias
    // =========================================================

    @GetMapping("/listarCategorias")
    public List<CategoriaDto> listarCategorias() {
        return categoriaService.vizualizarCategoria();
    }


    // =========================================================
    // CREAR CATEGORÍA
    // POST /api/categorias/crearCategoria
    // =========================================================

    @PostMapping("/crearCategoria")
    public CategoriaDto crearCategoria(@RequestBody CategoriaCreateDto dto) {

        return categoriaService.crearCategoria(dto);
    }


    // =========================================================
    // ELIMINAR CATEGORÍA
    // DELETE /api/categorias/eliminarCategoria/{id}
    // =========================================================

    @DeleteMapping("/eliminarCategoria/{id}")
    public void eliminarCategoria(@PathVariable Long id) {

        categoriaService.quitarCategoria(id);
    }
   

}
