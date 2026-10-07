package gym.ada.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diagnostico")
public class DiagnosticoController {

    @GetMapping("/ping")
    public String ping() {

        System.out.println("🔥 DIAGNOSTICO: Spring Boot recibió /api/diagnostico/ping");

        return "SPRING_BOOT_OK";
    }
    
    @PostMapping("/post")
    public String post() {

        System.out.println(
            "🔥 DIAGNOSTICO: Spring recibió POST"
        );

        return "POST_OK";
    }
}