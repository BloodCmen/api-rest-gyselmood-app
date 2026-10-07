package gym.ada.api.service.implement;

import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;

import gym.ada.api.service.ICloudinaryService;

@Service
public class CloudinaryServiceImplement implements ICloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImplement(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public void eliminarImagen(String publicId) {

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    new java.util.HashMap<>()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error eliminando imagen de Cloudinary",
                    e
            );
        }
    }
}