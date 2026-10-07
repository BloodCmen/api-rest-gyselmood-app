package gym.ada.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor

public class ImagenDto {
	
	private Long id;
    private String url;
    private String publicId;
	private boolean esPrincipal;
	
	
    public String getPublicId() {
		return publicId;
	}
	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isEsPrincipal() {
		return esPrincipal;
	}
	public void setEsPrincipal(boolean esPrincipal) {
		this.esPrincipal = esPrincipal;
	}
    
    

}
