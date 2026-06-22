package com.callerIdApplication.controller;

import com.callerIdApplication.entity.Report;
import com.callerIdApplication.entity.User;
import com.callerIdApplication.entity.SearchHistory;
import com.callerIdApplication.entity.SmsReport;
import com.callerIdApplication.repostitory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private SessionDao sessionDao;
    
    @Autowired
    private ReportDao reportDao;

    @Autowired
    private HistoryDao historyDao; // Para el feed de actividad

    @Autowired
    private SmsDao smsDao; // Debes tener este repositorio para los reportes de SMS

    private static final String ADMIN_PASSWORD = "admin123";

    // --- SEGURIDAD: MÉTODO AUXILIAR ---
    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("admin_logged") != null;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String password, HttpSession session) {
        if (ADMIN_PASSWORD.equals(password)) {
            session.setAttribute("admin_logged", true);
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/login?error=true";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";

        // Estadísticas principales
        model.addAttribute("totalUsers", userDao.count());
        model.addAttribute("activeSessions", sessionDao.count());
        model.addAttribute("totalReports", reportDao.count());
        model.addAttribute("totalSmsSpam", smsDao.count());

        // Feed de actividad reciente (Últimos 10 movimientos en la app)
        List<SearchHistory> recentActivity = historyDao.findAll();
        model.addAttribute("recentHistory", recentActivity.stream()
                .limit(10)
                .collect(Collectors.toList()));

        model.addAttribute("page", "admin/dashboard");
        return "admin/layout";
    }

    @GetMapping("/numbers")
    public String listNumbers(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        
        model.addAttribute("users", userDao.findAll());
        model.addAttribute("page", "admin/numbers");
        return "admin/layout";
    }

    // 🛡️ VERIFICAR USUARIO (Añadir escudo oficial)
    @PostMapping("/user/{id}/verify")
    public String verifyUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        
        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.getUserName().contains("🛡️")) {
                user.setUserName("🛡️ " + user.getUserName());
                userDao.save(user);
            }
        }
        return "redirect:/admin/numbers";
    }

    @GetMapping("/reports")
    public String listReports(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        
        model.addAttribute("reports", reportDao.findAll());
        model.addAttribute("page", "admin/reports");
        return "admin/layout";
    }

    // 📥 NUEVO: LISTAR REPORTES DE SMS SPAM
    @GetMapping("/sms-reports")
    public String listSmsReports(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        
        model.addAttribute("smsReports", smsDao.findAll());
        model.addAttribute("page", "admin/sms-reports");
        return "admin/layout";
    }

    @PostMapping("/sms-reports/{id}/delete")
    public String deleteSmsReport(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        smsDao.deleteById(id);
        return "redirect:/admin/sms-reports";
    }

    @PostMapping("/reports/{id}/toggle-spam")
    public String toggleSpam(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        
        try {
            Optional<Report> reportOpt = reportDao.findById(id);
            if (reportOpt.isPresent()) {
                Report report = reportOpt.get();
                report.setSpammer(!report.isSpammer());
                reportDao.save(report);
            }
        } catch (Exception e) {
            System.out.println("Error toggling spam: " + e.getMessage());
        }
        return "redirect:/admin/reports";
    }

    @GetMapping("/fix-uuid/{phoneNumber}")
    @ResponseBody
    public String fixUuid(@PathVariable String phoneNumber) {
        try {
            User user = userDao.findByphoneNumber(phoneNumber);
            if (user != null) {
                if (user.getUuid() == null || user.getUuid().isEmpty()) {
                    String newUuid = java.util.UUID.randomUUID().toString().substring(0, 8);
                    user.setUuid(newUuid);
                    userDao.save(user);
                    return "✅ UUID asignado: " + newUuid + " para el número " + phoneNumber;
                } else {
                    return "ℹ️ El usuario ya tiene UUID: " + user.getUuid();
                }
            }
            return "❌ Usuario no encontrado";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
