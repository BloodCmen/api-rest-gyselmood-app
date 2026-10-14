
package gym.ada.api.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import gym.ada.api.model.Pedido;
import gym.ada.api.repository.IPedidoRepository;
import gym.ada.api.service.implement.MercadoPagoWebhookService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/mercadopago")
public class MercadoPagoConciliacionController {

    private final MercadoPagoWebhookService mercadoPagoService;
    private final IPedidoRepository pedidoRepository;

    public MercadoPagoConciliacionController(
            MercadoPagoWebhookService mercadoPagoService,
            IPedidoRepository pedidoRepository
    ) {
        this.mercadoPagoService = mercadoPagoService;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping("/conciliar/{pedidoId}")
    public ResponseEntity<Map<String, Object>> conciliar(
            @PathVariable Long pedidoId,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        // 1. Exigir autenticación Firebase
        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Falta token Firebase"
            );
        }

        String idToken = authorization.substring(7).trim();

        FirebaseToken usuario;

        try {
            usuario = FirebaseAuth.getInstance()
                    .verifyIdToken(idToken);

        } catch (FirebaseAuthException e) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token Firebase inválido"
            );
        }

        // 2. Buscar el pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"
                ));

        // 3. Verificar que el pedido pertenezca al usuario
        if (pedido.getFirebaseUid() == null
                || !pedido.getFirebaseUid().equals(usuario.getUid())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para consultar este pedido"
            );
        }

        // 4. Consultar Mercado Pago y verificar el pago
        String resultado = mercadoPagoService.conciliarPedido(pedidoId);

        // 5. Consultar el estado actualizado
        Pedido actualizado = pedidoRepository.findById(pedidoId)
                .orElseThrow();

        return ResponseEntity.ok(Map.of(
                "pedidoId", pedidoId,
                "estado", actualizado.getEstado().name(),
                "resultado", resultado
        ));
    }
}
