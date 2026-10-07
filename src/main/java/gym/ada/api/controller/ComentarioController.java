package gym.ada.api.controller;


import org.springframework.web.bind.annotation.*;

import gym.ada.api.model.Comentario;
import gym.ada.api.service.IComentarioService;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final IComentarioService comentarioService;

    public ComentarioController(IComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public List<Comentario> listar() {
        return comentarioService.listarActivos();
    }

    @PostMapping
    public Comentario guardar(@RequestBody Comentario comentario) {
        return comentarioService.guardar(comentario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        comentarioService.eliminar(id);
    }
    
    @PatchMapping("/{id}/respuesta")
    public Comentario responder(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        Comentario c = comentarioService.buscarPorId(id);
        if (c == null) throw new RuntimeException("Comentario no encontrado");
        c.setRespuestaAdmin(body.get("respuesta"));
        return comentarioService.guardar(c);
    }
}
