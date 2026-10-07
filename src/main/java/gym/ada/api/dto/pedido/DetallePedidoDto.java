package gym.ada.api.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DetallePedidoDto {

    @NotNull
    private Long productoId;

    @NotNull
    private Long tallaId;

    @NotNull
    @Min(1)
    private Integer cantidad;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getTallaId() {
        return tallaId;
    }

    public void setTallaId(Long tallaId) {
        this.tallaId = tallaId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}