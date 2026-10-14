
package gym.ada.api.service.implement;

import gym.ada.api.dto.PagoResponseDto;
import gym.ada.api.dto.RegistrarPagoDto;
import gym.ada.api.dto.mercadopago.PreferenciaPagoResponseDto;
import gym.ada.api.enums.EstadoPago;
import gym.ada.api.enums.EstadoPedido;
import gym.ada.api.model.Pedido;
import gym.ada.api.repository.IPagoRepository;
import gym.ada.api.repository.IPedidoRepository;
import gym.ada.api.service.IMercadoPagoService;
import gym.ada.api.service.IPagoService;



import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoServiceImplement implements IMercadoPagoService {

    private final IPedidoRepository pedidoRepository;
    private final IPagoRepository pagoRepository;
    private final IPagoService pagoService;
    private final RestClient restClient;
    
    private final Environment environment;

    public MercadoPagoServiceImplement(
            IPedidoRepository pedidoRepository,
            IPagoRepository pagoRepository,
            IPagoService pagoService,
            Environment environment
    ) {
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
        this.pagoService = pagoService;
        this.environment = environment;

        String token = System.getenv("MERCADOPAGO_ACCESS_TOKEN");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Falta MERCADOPAGO_ACCESS_TOKEN"
            );
        }

        this.restClient = RestClient.builder()
                .baseUrl("https://api.mercadopago.com")
                .defaultHeader(
                        "Authorization",
                        "Bearer " + token
                )
                .build();
    }

    // CREAR PREFERENCIA DE PAGO
    @Override
    public PreferenciaPagoResponseDto crearPreferencia(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"
                ));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pedido no está pendiente de pago"
            );
        }

        BigDecimal total = pedido.getTotal();

        if (total == null || total.signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El total del pedido debe ser mayor que cero"
            );
        }

        Map<String, Object> item = Map.of(
                "title", "Pedido GyselMood #" + pedidoId,
                "quantity", 1,
                "currency_id", "PEN",
                "unit_price", total
        );

        Map<String, Object> request = Map.of(
        	    "items", List.of(item),
        	    "external_reference",
        	    "GYSELMOOD-PEDIDO-" + pedidoId
        	);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> respuesta = restClient.post()
                    .uri("/checkout/preferences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            if (respuesta == null
                    || respuesta.get("id") == null
                    || respuesta.get("init_point") == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Mercado Pago no devolvió la preferencia"
                );
            }

            return new PreferenciaPagoResponseDto(
                    respuesta.get("id").toString(),
                    respuesta.get("init_point").toString()
            );

        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error al crear preferencia en Mercado Pago",
                    e
            );
        }
    }

    // VERIFICAR PAGO REALMENTE REGISTRADO EN MERCADO PAGO
    @Override
    public PagoResponseDto verificarPago(
            Long pedidoId,
            String paymentId
    ) {
        if (paymentId == null || !paymentId.matches("[0-9]+")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Identificador de pago inválido"
            );
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"
                ));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pedido no está pendiente"
            );
        }

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
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el pago en Mercado Pago",
                    e
            );
        }

        if (pagoMp == null || pagoMp.get("id") == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago no devolvió el pago"
            );
        }

        String idVerificado = pagoMp.get("id").toString();
        String estado = String.valueOf(pagoMp.get("status"));
        String moneda = String.valueOf(pagoMp.get("currency_id"));
        String referencia = String.valueOf(
                pagoMp.get("external_reference")
        );

        // Validar que consultamos exactamente la operación solicitada
        if (!paymentId.equals(idVerificado)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El identificador del pago no coincide"
            );
        }
        System.out.println("=== MERCADO PAGO DEBUG ===");
        System.out.println("Payment ID: " + pagoMp.get("id"));
        System.out.println("Estado: " + pagoMp.get("status"));
        System.out.println("Live mode: " + pagoMp.get("live_mode"));
        System.out.println("Moneda: " + pagoMp.get("currency_id"));
        System.out.println("Referencia: " + pagoMp.get("external_reference"));
        System.out.println("Monto: " + pagoMp.get("transaction_amount"));
        System.out.println(
        	    "Collector ID: " + pagoMp.get("collector_id")
        	);

        // Esta implementación se reserva a operaciones de prueba

        // ============================================
        // DIAGNOSTICO COMPLETO MERCADO PAGO - LOCAL
        // ============================================

        String collectorId = String.valueOf(
                pagoMp.get("collector_id")
        );

        String sellerTestId = System.getenv(
                "MERCADOPAGO_TEST_SELLER_ID"
        );

        // Verificamos perfiles SIN usar acceptsProfiles
        // para evitar el error que te aparece en STS.
        String[] perfilesActivos = environment.getActiveProfiles();

        boolean esEntornoDev = java.util.Arrays
                .asList(perfilesActivos)
                .contains("dev");

        boolean existeSellerId =
                sellerTestId != null
                && !sellerTestId.isBlank();

        boolean esVendedorDePrueba =
                existeSellerId
                && sellerTestId.trim().equals(collectorId.trim());

        Object liveModeObjeto = pagoMp.get("live_mode");

        boolean esPagoNoProductivo =
                Boolean.FALSE.equals(liveModeObjeto);

        String tokenConfigurado = System.getenv(
                "MERCADOPAGO_ACCESS_TOKEN"
        );

        boolean existeAccessToken =
                tokenConfigurado != null
                && !tokenConfigurado.isBlank();

        String referenciaEsperadaDebug =
                "GYSELMOOD-PEDIDO-" + pedidoId;

        String referenciaRecibidaDebug =
                String.valueOf(
                        pagoMp.get("external_reference")
                );

        String estadoRecibidoDebug =
                String.valueOf(pagoMp.get("status"));

        String monedaRecibidaDebug =
                String.valueOf(pagoMp.get("currency_id"));

        String montoRecibidoDebug =
                String.valueOf(pagoMp.get("transaction_amount"));

        System.out.println();
        System.out.println("===========================================");
        System.out.println("       DEBUG COMPLETO MERCADO PAGO");
        System.out.println("===========================================");

        // 1. DATOS DE SPRING BOOT
        System.out.println("[01] Perfiles activos: "
                + java.util.Arrays.toString(perfilesActivos));

        System.out.println("[02] Perfil dev activo: "
                + esEntornoDev);

        System.out.println("[03] Perfil por propiedad: "
                + environment.getProperty("spring.profiles.active"));

        // 2. VARIABLES DE ENTORNO
        System.out.println("[04] Access Token configurado: "
                + existeAccessToken);

        System.out.println("[05] Seller Test ID existe: "
                + existeSellerId);

        System.out.println("[06] Seller ID configurado: "
                + (existeSellerId ? sellerTestId.trim() : "NO CONFIGURADO"));

        System.out.println("[07] Seller ID via Spring Environment: "
                + environment.getProperty(
                        "MERCADOPAGO_TEST_SELLER_ID",
                        "NO CONFIGURADO"
                ));

        // 3. IDENTIDAD DE LA OPERACION
        System.out.println("[08] Payment ID solicitado: "
                + paymentId);

        System.out.println("[09] Payment ID recibido: "
                + pagoMp.get("id"));

        System.out.println("[10] Collector ID: "
                + collectorId);

        System.out.println("[11] Coincide vendedor: "
                + esVendedorDePrueba);

        // 4. INFORMACION DE MERCADO PAGO
        System.out.println("[12] Estado MP: "
                + estadoRecibidoDebug);

        System.out.println("[13] Live mode: "
                + liveModeObjeto);

        System.out.println("[14] Tipo de live_mode: "
                + (liveModeObjeto == null
                    ? "NULL"
                    : liveModeObjeto.getClass().getName()));

        System.out.println("[15] Pago no productivo segun live_mode: "
                + esPagoNoProductivo);

        System.out.println("[16] Moneda: "
                + monedaRecibidaDebug);

        System.out.println("[17] Monto MP: "
                + montoRecibidoDebug);

        System.out.println("[18] Monto pedido: "
                + pedido.getTotal());

        // 5. REFERENCIAS
        System.out.println("[19] Referencia esperada: "
                + referenciaEsperadaDebug);

        System.out.println("[20] Referencia recibida: "
                + referenciaRecibidaDebug);

        System.out.println("[21] Coincide referencia: "
                + referenciaEsperadaDebug.equals(
                        referenciaRecibidaDebug));

        // 6. COMPROBACIONES DE VALIDACION
        System.out.println("[22] Estado aprobado: "
                + "approved".equalsIgnoreCase(
                        estadoRecibidoDebug));

        System.out.println("[23] Moneda PEN: "
                + "PEN".equals(monedaRecibidaDebug));

        boolean montoCoincide = false;

        try {
            BigDecimal montoDebug =
                    new BigDecimal(montoRecibidoDebug);

            montoCoincide =
                    pedido.getTotal() != null
                    && pedido.getTotal()
                            .compareTo(montoDebug) == 0;

        } catch (NumberFormatException e) {
            System.out.println(
                    "[ERROR] Monto recibido no es numerico"
            );
        }

        System.out.println("[24] Monto coincide: "
                + montoCoincide);

        // 7. ESTADO LOCAL
        System.out.println("[25] Pedido ID: "
                + pedidoId);

        System.out.println("[26] Estado del pedido: "
                + pedido.getEstado());

        System.out.println("[27] Pedido pendiente: "
                + (pedido.getEstado() == EstadoPedido.PENDIENTE));

        // 8. RESULTADO DEL BLOQUEO
        System.out.println("-------------------------------------------");

        System.out.println("[28] Falla perfil dev: "
                + !esEntornoDev);

        System.out.println("[29] Falla identidad vendedor: "
                + !esVendedorDePrueba);

        System.out.println("[30] Se bloqueara la operacion: "
                + (!esEntornoDev || !esVendedorDePrueba));

        System.out.println("===========================================");
        System.out.println();

        // Mantenemos el bloqueo mientras diagnosticamos
        if (!esEntornoDev || !esVendedorDePrueba) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La operacion no pertenece al entorno de pruebas configurado"
            );
        }

        // El pago debe estar aprobado por Mercado Pago
        if (!"approved".equalsIgnoreCase(estado)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mercado Pago no confirmó el pago: " + estado
            );
        }

        // Debe estar asociado al pedido correspondiente
        String referenciaEsperada =
                "GYSELMOOD-PEDIDO-" + pedidoId;

        if (!referenciaEsperada.equals(referencia)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pago pertenece a otro pedido"
            );
        }

        // Validar moneda
        if (!"PEN".equals(moneda)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La moneda del pago no coincide"
            );
        }

        // Validar importe exacto
        Object importe = pagoMp.get("transaction_amount");

        if (importe == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago no devolvió el importe"
            );
        }

        BigDecimal montoPagado;

        try {
            montoPagado = new BigDecimal(importe.toString());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Importe inválido devuelto por Mercado Pago"
            );
        }

        if (pedido.getTotal().compareTo(montoPagado) != 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El importe pagado no coincide con el pedido"
            );
        }

        // Evitar reutilizar una operación de Mercado Pago
        if (pagoRepository.existsByReferenciaExterna(idVerificado)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta operación ya fue registrada"
            );
        }

        // Evitar duplicar pagos para un mismo pedido
        if (pagoRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pedido ya tiene un pago registrado"
            );
        }

        RegistrarPagoDto dto = new RegistrarPagoDto();

        dto.setProveedor("MERCADO_PAGO");

        String metodo = String.valueOf(
                pagoMp.get("payment_type_id")
        );

        dto.setMetodo(
                "null".equals(metodo) ? "DESCONOCIDO" : metodo
        );

        dto.setEstado(EstadoPago.APROBADO);
        dto.setReferenciaExterna(idVerificado);

        // Reutilizar el servicio de pagos que ya tenemos
        return pagoService.registrarPago(pedidoId, dto);
    }
}
