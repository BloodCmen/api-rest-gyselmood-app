package gym.ada.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import gym.ada.api.model.Imagen;
import gym.ada.api.service.IImagenService;





@RestController // responde con JSON automaticamente
@RequestMapping("/api/imagenes")
public class ImagenController {
	
	private final IImagenService imagenService;
	
	public ImagenController (IImagenService imagenService ) {
		this.imagenService = imagenService;
	}

	@GetMapping("/listImagenes")
    public List< Imagen> listarTodos() {
        return imagenService.listarImagenes();
    }
    
 
    @GetMapping("/searchImagen/{id}")
    public Imagen buscarPorId(@PathVariable Long id) {
        return imagenService.buscarImagenesPorId(id);
    }
    
    @PostMapping("/saveImagen")
    public Imagen guardar(@RequestBody Imagen imagen) {
        return imagenService.guardarImagenes(imagen);
    }
    
   
    @DeleteMapping("/deleteImagen/{id}")
    public void eliminar(@PathVariable Long id) {
    	imagenService.eliminarImagenes(id);
    }
    
    //UTIL EN UN FUTURO 
 // GET http://localhost:8080/api/imagenes/producto/1
    @GetMapping("/producto/{productoId}")
    public List<Imagen> buscarPorProducto(@PathVariable Long productoId) {
        return imagenService.buscarPorProducto(productoId);
    }

    // GET http://localhost:8080/api/imagenes/producto/1/principal
    @GetMapping("/producto/{productoId}/principal")
    public Imagen buscarPrincipal(@PathVariable Long productoId) {
        return imagenService.buscarPrincipal(productoId);
}
}
