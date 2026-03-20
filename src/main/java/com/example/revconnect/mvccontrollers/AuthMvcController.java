package com.example.revconnect.mvccontrollers;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revconnect.entity.AccountType;
import com.example.revconnect.entity.SecurityQuestion;
import com.example.revconnect.entity.User;
import com.example.revconnect.service.UserService;

@Controller
public class AuthMvcController {

    @Autowired
    private UserService userService;

    // Email validation regex
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    @GetMapping({"/", "/login"})
    public String showLogin(Model model) {
        model.addAttribute("loginRequest", new User());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Basic input validation
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("errorMessage", "Email and password are required.");
            return "login";
        }

        // Email format validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            model.addAttribute("errorMessage", "Please enter a valid email address.");
            return "login";
        }

        try {

            // Validate credentials via service
            User user = userService.loginUser(email, password);

            if (user == null) {
                model.addAttribute("errorMessage", "Invalid email or password.");
                return "login";
            }

            return "redirect:/feed?userId=" + user.getUserId();

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid email or password.");
            return "login";
        }
    }

    /**
     * Show register page.
     */
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("securityQuestions", SecurityQuestion.values());
        return "register";
    }

    /**
     * Process registration form.
     */
    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute User user,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account created successfully! Please log in.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accountTypes", AccountType.values());
            model.addAttribute("securityQuestions", SecurityQuestion.values());
            model.addAttribute("user", user);
            return "register";
        }
    }

    /**
     * Show forgot password page.
     */
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    /**
     * Fetch security question for email.
     */
    @PostMapping("/forgot-password/question")
    public String getSecurityQuestion(
            @RequestParam String email,
            Model model) {

        try {
            String question = userService.getSecurityQuestion(email);
            model.addAttribute("email", email);
            model.addAttribute("question", question);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "No account found for that email.");
        }

        return "forgot-password";
    }

    /**
     * Reset password.
     */
    @PostMapping("/forgot-password/reset")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String answer,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {

            com.example.revconnect.dto.ResetPasswordRequest request =
                    new com.example.revconnect.dto.ResetPasswordRequest();

            request.setEmail(email);
            request.setAnswer(answer);
            request.setNewPassword(newPassword);

            userService.resetPassword(request);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Password reset successfully! Please log in.");

            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("email", email);
            return "forgot-password";
        }
    }

    /**
     * Logout.
     */
    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out.");
        return "redirect:/login";
    }
}