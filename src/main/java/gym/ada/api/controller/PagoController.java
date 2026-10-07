package gym.ada.api.controller;

import gym.ada.api.dto.PagoResponseDto;
import gym.ada.api.dto.RegistrarPagoDto;
import gym.ada.api.model.Pago;
import gym.ada.api.service.IPagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final IPagoService pagoService;

    public PagoController(IPagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDto> registrarPago(
            @PathVariable Long pedidoId,
            @Valid @RequestBody RegistrarPagoDto dto
    ) {

        PagoResponseDto pago = pagoService.registrarPago(
                pedidoId,
                dto
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pago);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDto> obtenerPago(
            @PathVariable Long pedidoId
    ) {

        return ResponseEntity.ok(
                pagoService.obtenerPago(pedidoId)
        );
    }
}