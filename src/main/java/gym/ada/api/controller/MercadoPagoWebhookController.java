
package gym.ada.api.controller;

import gym.ada.api.service.implement.MercadoPagoWebhookService;

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

	public MercadoPagoWebhookController(MercadoPagoWebhookService webhookService) {
		this.webhookService = webhookService;
	}

	@PostMapping
	public ResponseEntity<Void> recibirNotificacion(

			@RequestHeader(value = "x-signature", required = false) String xSignature,

			@RequestHeader(value = "x-request-id", required = false) String xRequestId,

			@RequestParam(value = "data.id", required = false) String dataId,

			@RequestParam(value = "type", required = false) String type,

			@RequestParam(value = "id", required = false) String ipnId,

			@RequestParam(value = "topic", required = false) String topic,

			@RequestBody(required = false) Map<String, Object> body) {

		System.out.println();
		System.out.println("========== WEBHOOK MERCADO PAGO ==========");

		String secreto = System.getenv("MERCADOPAGO_WEBHOOK_SECRET");

		boolean recuperacionPruebas = Boolean.parseBoolean(System.getenv("MERCADOPAGO_TEST_RECONCILE_UNSIGNED"));

		// Esta opcion debe usarse SOLO con una
		// cuenta vendedora de prueba configurada.
		boolean vendedorPruebaConfigurado = tieneTexto(System.getenv("MERCADOPAGO_TEST_SELLER_ID"));

		boolean recuperacionHabilitada = recuperacionPruebas && vendedorPruebaConfigurado;

		String bodyId = null;
		String bodyType = null;

		if (body != null) {

			bodyType = texto(body.get("type"));

			Object data = body.get("data");

			if (data instanceof Map<?, ?> datos) {
				bodyId = texto(datos.get("id"));
			}
		}

		// ======================================
		// 1. IDENTIFICAR FORMATO
		// ======================================

		boolean esWebhook = tieneTexto(dataId);
		boolean esIpn = !esWebhook && tieneTexto(ipnId);

		String paymentId = esWebhook ? dataId : ipnId;

		String tipoEvento = esWebhook ? (tieneTexto(type) ? type : bodyType) : topic;

		System.out.println("Formato: " + (esWebhook ? "WEBHOOK" : esIpn ? "IPN" : "DESCONOCIDO"));

		System.out.println("Payment ID: " + paymentId);
		System.out.println("Tipo: " + tipoEvento);

		if (!esWebhook && !esIpn) {

			System.out.println("WEBHOOK: identificador ausente");

			return ResponseEntity.badRequest().build();
		}

		if (!tieneTexto(paymentId) || !paymentId.matches("[0-9]+")) {

			System.out.println("WEBHOOK: identificador invalido");

			return ResponseEntity.badRequest().build();
		}

		if (!"payment".equalsIgnoreCase(tipoEvento)) {

			System.out.println("WEBHOOK: evento ignorado");

			return ResponseEntity.ok().build();
		}

		// Comprobar coherencia entre URL y cuerpo.
		if (esWebhook && tieneTexto(bodyId) && !dataId.equals(bodyId)) {

			System.out.println("WEBHOOK: IDs diferentes");

			return ResponseEntity.badRequest().build();
		}

		if (esWebhook && tieneTexto(type) && tieneTexto(bodyType) && !type.equalsIgnoreCase(bodyType)) {

			System.out.println("WEBHOOK: tipos inconsistentes");

			return ResponseEntity.badRequest().build();
		}

		// ======================================
		// 2. VALIDAR FIRMA
		// ======================================

		boolean firmaValida = false;

		if (esWebhook && tieneTexto(secreto) && tieneTexto(xSignature) && tieneTexto(xRequestId)) {

			firmaValida = validarFirma(xSignature, xRequestId, dataId, secreto);
		}

		System.out.println("========== DIAGNOSTICO HMAC ==========");

		System.out.println("Secret configurado: " + tieneTexto(secreto));

		System.out.println("X-Signature presente: " + tieneTexto(xSignature));

		System.out.println("X-Request-Id presente: " + tieneTexto(xRequestId));

		System.out.println("Data ID URL: " + dataId);

		System.out.println("Data ID BODY coincide: " + (tieneTexto(bodyId) && bodyId.equals(dataId)));

		if (tieneTexto(xSignature)) {

			boolean tieneTimestamp = xSignature.matches("(?s).*\\bts=[0-9]+.*");

			boolean tieneV1 = xSignature.matches("(?s).*\\bv1=[0-9a-fA-F]{64}.*");

			System.out.println("Timestamp presente: " + tieneTimestamp);
			System.out.println("Firma V1 presente: " + tieneV1);
		}

		System.out.println("=====================================");

		String secretoProduccion = System.getenv("MERCADOPAGO_WEBHOOK_SECRET_PROD_DIAG");

		boolean firmaConProduccion = false;

		if (esWebhook && tieneTexto(secretoProduccion) && tieneTexto(xSignature) && tieneTexto(xRequestId)) {

			firmaConProduccion = validarFirma(xSignature, xRequestId, dataId, secretoProduccion);
		}

		System.out.println("HMAC con clave PRUEBA: " + firmaValida);

		System.out.println("HMAC con clave PRODUCCION: " + firmaConProduccion);

		System.out.println("Firma HMAC valida: " + firmaValida);

		// ======================================
		// 3. DECIDIR SI PROCESAR
		// ======================================

		if (!firmaValida) {

			if (!recuperacionHabilitada) {

				System.out.println("WEBHOOK: firma no validada. " + "Procesamiento bloqueado.");

				if (!tieneTexto(secreto) && esWebhook) {
					return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
				}

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			// IMPORTANTE:
			// Esta ruta NO autentica la notificacion.
			// Solo permite consultar el pago mediante
			// el Access Token de nuestro backend.
			//
			// MercadoPagoWebhookService debe verificar:
			// - Identidad del vendedor de prueba
			// - Estado approved
			// - Referencia del pedido
			// - Moneda PEN
			// - Importe exacto
			// - Duplicados y estado del pedido

			System.out.println("MODO PRUEBA: recuperacion mediante " + "API oficial de Mercado Pago");
		} else {

			System.out.println("WEBHOOK: firma verificada correctamente");
		}

		// ======================================
		// 4. CONSULTAR Y REGISTRAR PAGO
		// ======================================

		System.out.println("Consultando pago: " + paymentId);

		String resultado = webhookService.procesarPago(paymentId);

		System.out.println("WEBHOOK RESULTADO: " + resultado);

		System.out.println("=====================================");

		return ResponseEntity.ok().build();
	}

	// ==========================================
	// 5. VALIDACION HMAC-SHA256
	// ==========================================

	private boolean validarFirma(String xSignature, String xRequestId, String paymentId, String secreto) {

		try {

			String timestamp = null;
			String firmaRecibida = null;

			for (String parte : xSignature.split(",")) {

				String[] datos = parte.trim().split("=", 2);

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

			if (!tieneTexto(timestamp) || !timestamp.matches("[0-9]+") || !tieneTexto(firmaRecibida)
					|| !firmaRecibida.matches("[0-9a-fA-F]{64}")) {

				return false;
			}

			String manifest = "id:" + paymentId.toLowerCase(Locale.ROOT) + ";request-id:" + xRequestId + ";ts:"
					+ timestamp + ";";

			Mac mac = Mac.getInstance("HmacSHA256");

			mac.init(new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

			byte[] calculada = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));

			byte[] recibida = HexFormat.of().parseHex(firmaRecibida);

			return MessageDigest.isEqual(calculada, recibida);

		} catch (Exception e) {

			System.err.println("WEBHOOK: error calculando firma: " + e.getClass().getSimpleName());

			return false;
		}
	}

	// ==========================================
	// 6. AUXILIARES
	// ==========================================

	private boolean tieneTexto(String valor) {
		return valor != null && !valor.isBlank();
	}

	private String texto(Object valor) {
		return valor == null ? null : valor.toString();
	}
}
