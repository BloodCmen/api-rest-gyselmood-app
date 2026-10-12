
package gym.ada.api.controller;

import gym.ada.api.dto.mercadopago.PreferenciaPagoResponseDto;
import gym.ada.api.service.IMercadoPagoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mercadopago")
public class MercadoPagoController {

    private final IMercadoPagoService mercadoPagoService;

    public MercadoPagoController(
            IMercadoPagoService mercadoPagoService
    ) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/preferencia/{pedidoId}")
    public ResponseEntity<PreferenciaPagoResponseDto> crearPreferencia(
            @PathVariable Long pedidoId
    ) {
        return ResponseEntity.ok(
                mercadoPagoService.crearPreferencia(pedidoId)
        );
    }
}
