package gym.ada.api.controller;


import gym.ada.api.dto.CrearPedidoDto;
import gym.ada.api.dto.pedido.ActualizarEstadoPedidoDto;
import gym.ada.api.dto.pedido.HistorialEstadoResponseDto;
import gym.ada.api.dto.pedido.PedidoResponseDto;
import gym.ada.api.service.IPedidoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;

    public PedidoController(IPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // CREAR PEDIDO
    @PostMapping
    public ResponseEntity<PedidoResponseDto> crearPedido(
            @Valid @RequestBody CrearPedidoDto dto
    ) {

        PedidoResponseDto pedido = pedidoService.crearPedido(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedido);
    }

    // LISTAR PEDIDOS DE UN USUARIO
    @GetMapping("/usuario/{firebaseUid}")
    public ResponseEntity<List<PedidoResponseDto>> listarPedidosUsuario(
            @PathVariable String firebaseUid
    ) {

        return ResponseEntity.ok(
                pedidoService.listarPedidosUsuario(firebaseUid)
        );
    }

    // OBTENER UN PEDIDO
    @GetMapping("/{id}/usuario/{firebaseUid}")
    public ResponseEntity<PedidoResponseDto> obtenerPedido(
            @PathVariable Long id,
            @PathVariable String firebaseUid
    ) {

        return ResponseEntity.ok(
                pedidoService.obtenerPedido(id, firebaseUid)
        );
    }

    // CANCELAR PEDIDO
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarPedido(
            @PathVariable Long id,
            @RequestParam String firebaseUid
    ) {

        pedidoService.cancelarPedido(id, firebaseUid);

        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoPedidoDto dto
    ) {

        pedidoService.actualizarEstado(id, dto);

        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/{id}/usuario/{firebaseUid}/historial")
    public ResponseEntity<List<HistorialEstadoResponseDto>> obtenerHistorial(
            @PathVariable Long id,
            @PathVariable String firebaseUid
    ) {
        return ResponseEntity.ok(
                pedidoService.obtenerHistorial(id, firebaseUid)
        );
    }
    
}