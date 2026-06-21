package com.callerIdApplication.controller;

import com.callerIdApplication.entity.Report;
import com.callerIdApplication.entity.User;
import com.callerIdApplication.repostitory.ReportDao;
import com.callerIdApplication.repostitory.SessionDao;
import com.callerIdApplication.repostitory.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserDao userDao;
    @Autowired private SessionDao sessionDao;
    @Autowired private ReportDao reportDao;
    
    private static final String ADMIN_PASSWORD = "admin123";
    
    // ... [Métodos de Login, Dashboard, Logout se mantienen igual] ...

    @GetMapping("/numbers")
    public String listNumbers(Model model, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        model.addAttribute("users", userDao.findAll());
        model.addAttribute("page", "admin/numbers");
        return "admin/layout";
    }

    // ========== NUEVO: Acción para Marcar Usuario como Spam ==========
    @PostMapping("/numbers/{userId}/mark-spam")
    public String markUserAsSpam(@PathVariable Long userId, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        
        try {
            Optional<User> userOpt = userDao.findById(userId);
            if (userOpt.isPresent()) {
                // Creamos un nuevo reporte de spam para este usuario
                Report report = new Report();
                report.setPhoneNumber(userOpt.get().getPhoneNumber());
                report.setSpammer(true);
                report.setComment("Marcado como spam manualmente por Admin");
                reportDao.save(report);
            }
        } catch (Exception e) {
            System.out.println("Error marcando usuario como spam: " + e.getMessage());
        }
        return "redirect:/admin/numbers";
    }

    // ========== Lógica existente para reportes ==========
    @GetMapping("/reports")
    public String listReports(Model model, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        model.addAttribute("reports", reportDao.findAll());
        model.addAttribute("page", "admin/reports");
        return "admin/layout";
    }
    
    @PostMapping("/reports/{id}/toggle-spam")
    public String toggleSpam(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        
        reportDao.findById(id).ifPresent(report -> {
            report.setSpammer(!report.isSpammer());
            reportDao.save(report);
        });
        return "redirect:/admin/reports";
    }

    // ... [Tu método fixUuid se mantiene igual] ...
}
