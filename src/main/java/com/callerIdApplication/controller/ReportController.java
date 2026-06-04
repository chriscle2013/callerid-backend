@PostMapping("/report-with-name")
public ResponseEntity<Map<String, Object>> reportNumberWithName(@RequestBody Map<String, Object> reportData) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        String phoneNumber = (String) reportData.get("phoneNumber");
        String category = (String) reportData.get("category");
        String comment = (String) reportData.get("comment");
        String assignedName = (String) reportData.get("assignedName");
        
        // Guardar reporte
        Report report = new Report(phoneNumber, category, comment);
        report.setSpammer(true);
        reportDao.save(report);
        
        // Actualizar o crear Spam
        List<Spam> spamList = spamDao.findBynumber(phoneNumber);
        Spam spam;
        if (spamList != null && !spamList.isEmpty()) {
            spam = spamList.get(0);
            spam.setSpammer(true);
            if (assignedName != null && !assignedName.isEmpty()) {
                spam.setName(assignedName);
            }
        } else {
            spam = new Spam();
            spam.setNumber(phoneNumber);
            spam.setSpammer(true);
            spam.setName(assignedName != null ? assignedName : "Unknown");
        }
        spamDao.save(spam);
        
        response.put("success", true);
        response.put("message", "Reporte enviado correctamente");
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "Error: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
