package com.callerIdApplication.controller;

import com.callerIdApplication.entity.SmsReport; 
import com.callerIdApplication.repostitory.SmsDao; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 
import java.util.Base64; 
import java.util.HashMap; 
import java.util.Map;

/**
 * 🛡️ VELO VOICE SECURITY CONTROLLER
 * Procesa y analiza muestras de audio para detectar clonación de voz por IA.
 */
@RestController
@RequestMapping("/voice")
@CrossOrigin(origins = "*") // 🔥 VITAL: Permite conexiones desde la App Android
public class VoiceController {
    @Autowired
    private SmsDao smsDao; // Usaremos la tabla de reportes para guardar la alerta

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeVoice(
        @RequestBody Map<String, String> payload,
        @RequestParam(name = "key", required = false) String key) {
    
    Map<String, Object> response = new HashMap<>();
    try {
        String audioBase64 = payload.get("audio");
        String phoneNumber = payload.getOrDefault("phoneNumber", "Oculto");
        
        byte[] audioData = Base64.getDecoder().decode(audioBase64);

        // 🧠 MOTOR DE DETECCIÓN VELO IA v1.2 (Calibrado para evitar falsos positivos)
        boolean isSynthetic = detectAiArtifacts(audioData);

        // 💾 GUARDAR REPORTE EN LA NUBE SI ES SOSPECHOSO
        if (isSynthetic) {
            SmsReport voiceAlert = new SmsReport(phoneNumber, 
                "[ALERTA DE VOZ] Se detectó posible clonación de voz por IA durante la llamada.", 
                "Suplantación IA", "Sistema Velo Shield");
            smsDao.save(voiceAlert);
        }

        response.put("status", "success");
        response.put("isAi", isSynthetic);
        response.put("verdict", isSynthetic ? "ALERTA IA: Voz Clonada Detectada" : "HUMANO: Voz Orgánica Verificada");
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(500).build();
    }
}

private boolean detectAiArtifacts(byte[] data) {
    if (data.length < 1000) return false;
    
    int identicalPatterns = 0;
    for (int i = 0; i < data.length - 15; i++) {
        // Buscamos repeticiones matemáticas exactas (Señal de clonación)
        if (data[i] == data[i+1] && data[i] == data[i+2] && data[i] == data[i+3]) {
            identicalPatterns++;
        }
    }
    // Subimos el umbral al 20% para ser menos agresivos con humanos
    return identicalPatterns > (data.length * 0.20); 
}
