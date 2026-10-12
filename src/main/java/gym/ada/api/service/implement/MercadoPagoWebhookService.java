
package gym.ada.api.service.implement;

import gym.ada.api.dto.RegistrarPagoDto;
import gym.ada.api.enums.EstadoPago;
import gym.ada.api.enums.EstadoPedido;
import gym.ada.api.model.Pedido;
import gym.ada.api.repository.IPagoRepository;
import gym.ada.api.repository.IPedidoRepository;
import gym.ada.api.service.IPagoService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;



import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MercadoPagoWebhookService {

    private static final Pattern REFERENCIA =
            Pattern.compile("^GYSELMOOD-PEDIDO-([0-9]+)$");

    private final IPedidoRepository pedidoRepository;
    private final IPagoRepository pagoRepository;
    private final IPagoService pagoService;
    private final RestClient restClient;

    public MercadoPagoWebhookService(
            IPedidoRepository pedidoRepository,
            IPagoRepository pagoRepository,
            IPagoService pagoService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
        this.pagoService = pagoService;

        String token = System.getenv("MERCADOPAGO_ACCESS_TOKEN");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Falta MERCADOPAGO_ACCESS_TOKEN");
        }

        this.restClient = RestClient.builder()
                .baseUrl("https://api.mercadopago.com")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    public String procesarPago(String paymentId) {

        if (paymentId == null
                || !paymentId.matches("[0-9]+")) {
            return "IGNORADO_ID_INVALIDO";
        }

        // 1. Consultar la operacion directamente a MP
        Map<String, Object> pagoMp;

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> respuesta = restClient.get()
                    .uri("/v1/payments/{id}", paymentId)
                    .retrieve()
                    .body(Map.class);

            pagoMp = respuesta;

        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar Mercado Pago",
                    e
            );
        }

        if (pagoMp == null || pagoMp.get("id") == null) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Respuesta incompleta de Mercado Pago"
            );
        }

        // 2. Comprobar identificador y vendedor
        String idReal = String.valueOf(pagoMp.get("id"));
        String collectorId =
                String.valueOf(pagoMp.get("collector_id"));

        String vendedorPermitido =
                System.getenv("MERCADOPAGO_TEST_SELLER_ID");

        if (vendedorPermitido == null
                || vendedorPermitido.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Falta configurar el vendedor de pruebas"
            );
        }

        if (!paymentId.equals(idReal)
                || !vendedorPermitido.trim().equals(collectorId)) {
            return "IGNORADO_VENDEDOR_O_ID";
        }

        // Este servicio esta reservado para la cuenta
        // vendedora de prueba configurada en Railway.
        // No habilitar para pagos productivos sin adaptar
        // el modelo de credenciales y validaciones.

        // 3. Comprobar que el pago este aprobado
        String estado = String.valueOf(pagoMp.get("status"));

        if (!"approved".equalsIgnoreCase(estado)) {
            return "IGNORADO_ESTADO_" + estado;
        }

        // 4. Extraer el ID del pedido de external_reference
        String referencia =
                String.valueOf(pagoMp.get("external_reference"));

        Matcher matcher = REFERENCIA.matcher(referencia);

        if (!matcher.matches()) {
            return "IGNORADO_REFERENCIA";
        }

        Long pedidoId;

        try {
            pedidoId = Long.valueOf(matcher.group(1));
        } catch (NumberFormatException e) {
            return "IGNORADO_REFERENCIA_INVALIDA";
        }

        // 5. Buscar el pedido local
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElse(null);

        if (pedido == null) {
            return "IGNORADO_PEDIDO_INEXISTENTE";
        }

        // 6. Validar moneda e importe exacto
        if (!"PEN".equals(
                String.valueOf(pagoMp.get("currency_id")))) {
            return "IGNORADO_MONEDA";
        }

        Object importe = pagoMp.get("transaction_amount");

        if (importe == null || pedido.getTotal() == null) {
            return "IGNORADO_MONTO_AUSENTE";
        }

        BigDecimal montoMp;

        try {
            montoMp = new BigDecimal(importe.toString());
        } catch (NumberFormatException e) {
            return "IGNORADO_MONTO_INVALIDO";
        }

        if (pedido.getTotal().compareTo(montoMp) != 0) {
            return "IGNORADO_MONTO_DISTINTO";
        }

        // 7. Comprobar duplicados antes de registrar
        if (pagoRepository.existsByReferenciaExterna(idReal)) {
            return "YA_PROCESADO";
        }

        if (pagoRepository.findByPedidoId(pedidoId).isPresent()) {
            return "PEDIDO_CON_PAGO_EXISTENTE";
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            return "IGNORADO_ESTADO_PEDIDO";
        }

        // 8. Preparar el pago con datos del proveedor
        RegistrarPagoDto dto = new RegistrarPagoDto();

        dto.setProveedor("MERCADO_PAGO");

        String metodo =
                String.valueOf(pagoMp.get("payment_type_id"));

        dto.setMetodo(
                "null".equals(metodo)
                ? "DESCONOCIDO"
                : metodo
        );

        dto.setEstado(EstadoPago.APROBADO);
        dto.setReferenciaExterna(idReal);

        // 9. Registrar con nuestro servicio existente
        pagoService.registrarPago(pedidoId, dto);

        System.out.println(
                "MERCADO PAGO: pedido #" + pedidoId
                + " actualizado a PAGADO"
        );

        return "PAGO_REGISTRADO";
    }
}
