package gym.ada.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gym.ada.api.dto.TallaCreateDto;
import gym.ada.api.dto.TallaListDto;
import gym.ada.api.model.Talla;
import gym.ada.api.service.ITallaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;




@RestController // responde con JSON automaticamente
@RequestMapping("/api/tallas")
@SecurityRequirement(name = "Bearer Authentication")
public class TallaController {

	
    private final ITallaService tallaService;
	
	public TallaController(ITallaService TallaService) {
        this.tallaService = TallaService;
    }
	
	 
    @GetMapping("/listTallas")
    public List<Talla> listarTodos() {
        return tallaService.listarTallas();
    }
    
 
    @GetMapping("/searchTalla/{id}")
    public Talla buscarPorId(@PathVariable Long id) {
        return tallaService.buscarTallasPorId(id);
    }
    
    @PostMapping("/saveTalla")
    public Talla guardar(@RequestBody Talla talla) {
        return tallaService.guardarTallas(talla);
    }
    
  
    @DeleteMapping("/deleteTalla/{id}")
    public void eliminar(@PathVariable Long id) {
    	tallaService.eliminarTallas(id);
    }
    
    
  
    
 // =========================================================
    // 1. LISTAR TALLAS -> GET /api/tallas
    // =========================================================
    @GetMapping("/listarTallas")
    public ResponseEntity<List<TallaListDto>> listarTallas() {
        List<TallaListDto> tallas = tallaService.vizualizarTallas();
        return ResponseEntity.ok(tallas);
    }

    // =========================================================
    // 2. CREAR TALLA -> POST /api/tallas
    // =========================================================
    @PostMapping("/guardarTalla")
    public ResponseEntity<Talla> crearTalla(@RequestBody TallaCreateDto dto) {
        Talla nuevaTalla = tallaService.crearTalla(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTalla);
    }

    // =========================================================
    // 3. ELIMINAR TALLA -> DELETE /api/tallas/{id}
    // =========================================================
    @DeleteMapping("/eliminarTalla/{id}")
    public ResponseEntity<Void> eliminarTalla(@PathVariable Long id) {
        tallaService.eliminarTalla(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
    
    
    
    
}
