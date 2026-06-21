package com.callerIdApplication.controller;

import com.callerIdApplication.entity.Report;
import com.callerIdApplication.entity.User;
import com.callerIdApplication.repostitory.ReportDao;
import com.callerIdApplication.repostitory.SessionDao;
import com.callerIdApplication.repostitory.SmsRepository;
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

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private SessionDao sessionDao;
    
    @Autowired
    private ReportDao reportDao;

    @Autowired
    private SmsRepository smsRepository;
    
    private static final String ADMIN_PASSWORD = "admin123";
    
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
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        
        model.addAttribute("totalUsers", userDao.count());
        model.addAttribute("activeSessions", sessionDao.count());
        model.addAttribute("totalReports", reportDao.count());
        model.addAttribute("page", "admin/dashboard");
        return "admin/layout";
    }
    
    @GetMapping("/numbers")
    public String listNumbers(Model model, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        
        model.addAttribute("users", userDao.findAll());
        model.addAttribute("page", "admin/numbers");
        return "admin/layout";
    }
    
    @GetMapping("/reports")
    public String listReports(Model model, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        
        model.addAttribute("reports", reportDao.findAll());
        model.addAttribute("page", "admin/reports");
        return "admin/layout";
    }

    // Nuevo método para listar Reportes de SMS
    @GetMapping("/sms-reports")
    public String listSmsReports(Model model, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        
        model.addAttribute("smsReports", smsRepository.findAll());
        model.addAttribute("page", "admin/sms-reports");
        return "admin/layout";
    }
    
    @PostMapping("/reports/{id}/toggle-spam")
    public String toggleSpam(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin_logged") == null) return "redirect:/admin/login";
        
        Optional<Report> reportOpt = reportDao.findById(id);
        if (reportOpt.isPresent()) {
            Report report = reportOpt.get();
            report.setSpammer(!report.isSpammer());
            reportDao.save(report);
        }
        return "redirect:/admin/reports";
    }
    
    @GetMapping("/fix-uuid/{phoneNumber}")
    @ResponseBody
    public String fixUuid(@PathVariable String phoneNumber) {
        User user = userDao.findByphoneNumber(phoneNumber);
        if (user != null) {
            if (user.getUuid() == null || user.getUuid().isEmpty()) {
                String newUuid = java.util.UUID.randomUUID().toString().substring(0, 8);
                user.setUuid(newUuid);
                userDao.save(user);
                return "✅ UUID asignado: " + newUuid;
            }
            return "ℹ️ El usuario ya tiene UUID: " + user.getUuid();
        }
        return "❌ Usuario no encontrado";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
