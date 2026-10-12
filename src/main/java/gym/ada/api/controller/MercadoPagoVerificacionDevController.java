
package gym.ada.api.controller;

import gym.ada.api.dto.PagoResponseDto;
import gym.ada.api.service.IMercadoPagoService;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile("dev")
@RestController
@RequestMapping("/api/dev/mercadopago")
public class MercadoPagoVerificacionDevController {

    private final IMercadoPagoService mercadoPagoService;

    public MercadoPagoVerificacionDevController(
            IMercadoPagoService mercadoPagoService
    ) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/verificar/{pedidoId}/{paymentId}")
    public ResponseEntity<PagoResponseDto> verificarPago(
            @PathVariable Long pedidoId,
            @PathVariable String paymentId
    ) {
        return ResponseEntity.ok(
                mercadoPagoService.verificarPago(pedidoId, paymentId)
        );
    }
}
