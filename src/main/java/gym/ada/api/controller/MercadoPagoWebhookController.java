
package gym.ada.api.controller;

import java.nio.charset.StandardCharsets;
import gym.ada.api.service.implement.MercadoPagoWebhookService;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;

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
            @RequestHeader(value = "x-signature", required = false)
            String xSignature,

            @RequestHeader(value = "x-request-id", required = false)
            String xRequestId,

            @RequestParam(value = "data.id", required = false)
            String paymentId,

            @RequestParam(value = "type", required = false)
            String tipo
    ) {

        String secreto = System.getenv("MERCADOPAGO_WEBHOOK_SECRET");

        if (secreto == null || secreto.isBlank()) {
            System.err.println("WEBHOOK: falta configurar el secreto");
            return ResponseEntity.status(
                    HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        if (paymentId == null || paymentId.isBlank()
                || xSignature == null || xSignature.isBlank()
                || xRequestId == null || xRequestId.isBlank()) {

            System.out.println("WEBHOOK: faltan datos obligatorios");
            return ResponseEntity.badRequest().build();
        }

        if (!validarFirma(
                xSignature, xRequestId, paymentId, secreto)) {

            System.out.println("WEBHOOK: firma invalida");
            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED).build();
        }

        if (!"payment".equalsIgnoreCase(tipo)) {
            System.out.println("WEBHOOK: evento ignorado");
            return ResponseEntity.ok().build();
        }

        if (!paymentId.matches("[0-9]+")) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("==================================");
        System.out.println("WEBHOOK MERCADO PAGO RECIBIDO");
        System.out.println("Firma verificada correctamente");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("==================================");

        String resultado = webhookService.procesarPago(paymentId);

        System.out.println(
                "WEBHOOK RESULTADO: " + resultado
        );

        return ResponseEntity.ok().build();
    }

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
                String[] datos = parte.trim().split("=", 2);

                if (datos.length != 2) {
                    continue;
                }

                if ("ts".equals(datos[0].trim())) {
                    timestamp = datos[1].trim();
                } else if ("v1".equals(datos[0].trim())) {
                    firmaRecibida = datos[1].trim();
                }
            }

            if (timestamp == null
                    || !timestamp.matches("[0-9]+")
                    || firmaRecibida == null
                    || !firmaRecibida.matches("[0-9a-fA-F]{64}")) {
                return false;
            }

            String manifest =
                    "id:" + paymentId.toLowerCase(Locale.ROOT)
                    + ";request-id:" + xRequestId
                    + ";ts:" + timestamp + ";";

            Mac mac = Mac.getInstance("HmacSHA256");

            mac.init(new SecretKeySpec(
                    secreto.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));

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
            return false;
        }
    }
}
