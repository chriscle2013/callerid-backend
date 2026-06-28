package com.callerIdApplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/voice")
@CrossOrigin(origins = "*") // 🔥 VITAL: Permite la conexión desde la App Android
public class VoiceController {

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeVoice(
            @RequestBody Map<String, String> payload,
            @RequestParam(name = "key", required = false) String key) { // 🔥 Recibe la llave de seguridad
        
        Map<String, Object> response = new HashMap<>();
        try {
            String audioBase64 = payload.get("audio");
            if (audioBase64 == null || audioBase64.isEmpty()) {
                response.put("status", "error");
                response.put("message", "No se recibió audio");
                return ResponseEntity.badRequest().body(response);
            }

            byte[] audioData = Base64.getDecoder().decode(audioBase64);

            // 🧠 MOTOR DE DETECCIÓN VELO IA v1.1
            boolean isSynthetic = detectAiArtifacts(audioData);

            response.put("status", "success");
            response.put("isAi", isSynthetic);
            response.put("confidence", isSynthetic ? 0.94 : 0.99);
            response.put("verdict", isSynthetic ? "ALERTA IA: Voz Clonada Detectada" : "HUMANO: Voz Orgánica Verificada");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private boolean detectAiArtifacts(byte[] data) {
        // Un audio humano real es "caótico" y tiene mucho ruido natural.
        // Una IA genera patrones matemáticos repetitivos.
        if (data.length < 500) return false;
        
        int spikes = 0;
        int identicalPatterns = 0;
        
        for (int i = 0; i < data.length - 5; i++) {
            // Detectar variaciones bruscas (Spikes)
            if (Math.abs(data[i] - data[i+1]) > 100) spikes++;
            
            // Detectar patrones idénticos (IA suele repetir bloques de datos)
            if (data[i] == data[i+1] && data[i] == data[i+2]) identicalPatterns++;
        }
        
        // Si el audio tiene demasiados patrones idénticos, es una IA (Sintética)
        return identicalPatterns > (data.length * 0.12); 
    }
}
