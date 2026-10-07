package gym.ada.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void inicializarFirebase() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            InputStream serviceAccount = null;

            // 1. Buscar primero la variable de entorno de Railway (FIREBASE_CREDENTIALS)
            String firebaseJsonEnv = System.getenv("FIREBASE_CREDENTIALS");

            if (firebaseJsonEnv != null && !firebaseJsonEnv.trim().isEmpty()) {
                // Si existe la variable en Railway, convertimos el JSON (texto) a InputStream
                serviceAccount = new ByteArrayInputStream(firebaseJsonEnv.getBytes(StandardCharsets.UTF_8));
            } else {
                // 2. Si es en local, leer el archivo desde el classpath
                serviceAccount = getClass()
                        .getClassLoader()
                        .getResourceAsStream("firebase/gyselmoodapp-firebase-adminsdk-fbsvc-5dfd0a881c.json");
            }

            if (serviceAccount == null) {
                throw new RuntimeException(
                        "No se encontraron las credenciales de Firebase en la variable FIREBASE_CREDENTIALS ni en el archivo local."
                );
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            System.out.println("Firebase Admin inicializado correctamente");

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar Firebase Admin", e);
        }
    }
}