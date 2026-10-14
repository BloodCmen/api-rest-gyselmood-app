
package gym.ada.api.controller;

import gym.ada.api.service.implement.MercadoPagoWebhookService;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mercadopago/webhook")
public class MercadoPagoWebhookController {

    private final MercadoPagoWebhookService webhookService;

    public MercadoPagoWebhookController(
            MercadoPagoWebhookService webhookService
    ) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<Void> recibirNotificacion(

            @RequestHeader(
                    value = "x-signature",
                    required = false
            )
            String xSignature,

            @RequestHeader(
                    value = "x-request-id",
                    required = false
            )
            String xRequestId,

            @RequestParam(
                    value = "data.id",
                    required = false
            )
            String paymentId,

            @RequestParam(
                    value = "type",
                    required = false
            )
            String tipo,

            @RequestBody(required = false)
            Map<String, Object> body,

            HttpServletRequest request
    ) {

        // =========================================
        // 1. DIAGNOSTICO DE NOTIFICACION
        // =========================================

        System.out.println();
        System.out.println("========== DEBUG WEBHOOK MP ==========");

        System.out.println("[01] Metodo: "
                + request.getMethod());

        System.out.println("[02] Ruta: "
                + request.getRequestURI());

        // Solo los nombres de los parametros,
        // nunca valores secretos.
        System.out.println("[03] Parametros URL: "
                + request.getParameterMap().keySet());

        System.out.println("[04] Query data.id: "
                + paymentId);

        System.out.println("[05] Query type: "
                + tipo);

        System.out.println("[06] x-signature presente: "
                + tieneTexto(xSignature));

        System.out.println("[07] x-request-id presente: "
                + tieneTexto(xRequestId));

        String secreto = System.getenv(
                "MERCADOPAGO_WEBHOOK_SECRET"
        );

        System.out.println("[08] Secreto configurado: "
                + tieneTexto(secreto));

        String bodyId = null;
        String bodyType = null;

        if (body != null) {

            bodyType = convertirTexto(body.get("type"));

            System.out.println("[09] Body type: "
                    + bodyType);

            System.out.println("[10] Body action: "
                    + body.get("action"));

            System.out.println("[11] Body live_mode: "
                    + body.get("live_mode"));

            Object data = body.get("data");

            if (data instanceof Map<?, ?> dataMap) {

                bodyId = convertirTexto(dataMap.get("id"));

                System.out.println("[12] Body data.id: "
                        + bodyId);

            } else {
                System.out.println(
                        "[12] Body data.id ausente"
                );
            }

        } else {
            System.out.println("[09] Body ausente");
        }

        boolean coincidenIds =
                tieneTexto(paymentId)
                && tieneTexto(bodyId)
                && paymentId.equals(bodyId);

        System.out.println("[13] Coinciden IDs URL/body: "
                + coincidenIds);

        System.out.println("======================================");

        // =========================================
        // 2. VERIFICAR CONFIGURACION
        // =========================================

        if (!tieneTexto(secreto)) {

            System.err.println(
                    "WEBHOOK: falta MERCADOPAGO_WEBHOOK_SECRET"
            );

            return ResponseEntity.status(
                    HttpStatus.SERVICE_UNAVAILABLE
            ).build();
        }

        // =========================================
        // 3. VALIDAR CAMPOS OBLIGATORIOS
        // =========================================

        if (!tieneTexto(paymentId)
                || !tieneTexto(xSignature)
                || !tieneTexto(xRequestId)) {

            System.out.println(
                    "WEBHOOK: faltan campos necesarios "
                    + "para verificar la firma"
            );

            return ResponseEntity.badRequest().build();
        }

        // El ID de la URL es el que utiliza MP
        // para construir la firma HMAC.
        // No reemplazarlo por el ID del body.
        if (!paymentId.matches("[0-9]+")) {

            System.out.println(
                    "WEBHOOK: paymentId invalido"
            );

            return ResponseEntity.badRequest().build();
        }

        // Si ambos IDs estan presentes,
        // deben corresponder a la misma operacion.
        if (tieneTexto(bodyId)
                && !paymentId.equals(bodyId)) {

            System.out.println(
                    "WEBHOOK: ID del body no coincide "
                    + "con el ID de la URL"
            );

            return ResponseEntity.badRequest().build();
        }

        // =========================================
        // 4. VALIDAR FIRMA HMAC-SHA256
        // =========================================

        boolean firmaCorrecta = validarFirma(
                xSignature,
                xRequestId,
                paymentId,
                secreto
        );

        System.out.println("[14] Firma HMAC valida: "
                + firmaCorrecta);

        if (!firmaCorrecta) {

            System.out.println(
                    "WEBHOOK: firma invalida"
            );

            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED
            ).build();
        }

        // =========================================
        // 5. VALIDAR TIPO DE EVENTO
        // =========================================

        // MP puede informar el tipo tanto en
        // query params como en el cuerpo.
        // Aceptamos el tipo del body solo
        // despues de validar la firma.

        if (tieneTexto(tipo)
                && tieneTexto(bodyType)
                && !tipo.equalsIgnoreCase(bodyType)) {

            System.out.println(
                    "WEBHOOK: tipo de evento inconsistente"
            );

            return ResponseEntity.badRequest().build();
        }

        String tipoEvento = tieneTexto(tipo)
                ? tipo
                : bodyType;

        if (!"payment".equalsIgnoreCase(tipoEvento)) {

            System.out.println(
                    "WEBHOOK: evento ignorado: "
                    + tipoEvento
            );

            return ResponseEntity.ok().build();
        }

        // =========================================
        // 6. PROCESAR PAGO VERIFICADO
        // =========================================

        System.out.println();
        System.out.println("======================================");
        System.out.println("WEBHOOK MERCADO PAGO RECIBIDO");
        System.out.println("Firma verificada correctamente");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("======================================");

        String resultado = webhookService.procesarPago(
                paymentId
        );

        System.out.println(
                "WEBHOOK RESULTADO: " + resultado
        );

        // La consulta a Mercado Pago y las
        // validaciones se realizan en el servicio.
        return ResponseEntity.ok().build();
    }

    // =============================================
    // 7. VALIDACION DE FIRMA MERCADO PAGO
    // =============================================

    private boolean validarFirma(
            String xSignature,
            String xRequestId,
            String paymentId,
            String secreto
    ) {

        try {

            String timestamp = null;
            String firmaRecibida = null;

            for (String parte : xSignature.split(",")) {

                String[] datos =
                        parte.trim().split("=", 2);

                if (datos.length != 2) {
                    continue;
                }

                String clave = datos[0].trim();
                String valor = datos[1].trim();

                if ("ts".equals(clave)) {
                    timestamp = valor;
                }

                if ("v1".equals(clave)) {
                    firmaRecibida = valor;
                }
            }

            if (!tieneTexto(timestamp)
                    || !timestamp.matches("[0-9]+")
                    || !tieneTexto(firmaRecibida)
                    || !firmaRecibida.matches(
                            "[0-9a-fA-F]{64}"
                    )) {

                return false;
            }

            // Manifest definido por Mercado Pago.
            String manifest =
                    "id:"
                    + paymentId.toLowerCase(Locale.ROOT)
                    + ";request-id:"
                    + xRequestId
                    + ";ts:"
                    + timestamp
                    + ";";

            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec llave = new SecretKeySpec(
                    secreto.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );

            mac.init(llave);

            byte[] firmaCalculada = mac.doFinal(
                    manifest.getBytes(StandardCharsets.UTF_8)
            );

            byte[] firmaOriginal = HexFormat.of()
                    .parseHex(firmaRecibida);

            return MessageDigest.isEqual(
                    firmaCalculada,
                    firmaOriginal
            );

        } catch (Exception e) {

            System.err.println(
                    "WEBHOOK: error al validar HMAC: "
                    + e.getClass().getSimpleName()
            );

            return false;
        }
    }

    // =============================================
    // 8. METODOS AUXILIARES
    // =============================================

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private String convertirTexto(Object valor) {
        return valor == null
                ? null
                : valor.toString();
    }
}
