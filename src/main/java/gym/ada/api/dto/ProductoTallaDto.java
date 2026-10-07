package gym.ada.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class ProductoTallaDto {

    private Long tallaId;
    private Integer stock;
	public Long getTallaId() {
		return tallaId;
	}
	public void setTallaId(Long tallaId) {
		this.tallaId = tallaId;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
    
}