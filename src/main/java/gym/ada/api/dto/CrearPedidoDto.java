package gym.ada.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

import gym.ada.api.dto.pedido.DetallePedidoDto;

public class CrearPedidoDto {

    @NotBlank
    private String firebaseUid;

    @NotBlank
    private String direccionEntrega;

    private String referencia;

    @NotNull
    private BigDecimal costoEnvio;

    @Valid
    @NotEmpty
    private List<DetallePedidoDto> productos;

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public List<DetallePedidoDto> getProductos() {
        return productos;
    }

    public void setProductos(List<DetallePedidoDto> productos) {
        this.productos = productos;
    }
}