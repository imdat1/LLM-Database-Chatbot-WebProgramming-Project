package com.example.sql_chatbot.Web;

import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.exceptions.InvalidArgumentsException;
import com.example.sql_chatbot.Models.exceptions.InvalidUserCredentialsException;
import com.example.sql_chatbot.Service.AuthService;
import com.example.sql_chatbot.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final AuthService authService;
    private final UserService userService;

    public LoginController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping
    public String getLoginPage(Model model) {
        return "login.html";
    }

    @PostMapping
    public String login(HttpServletRequest request, Model model, HttpSession session) {
        User user = null;

        try {
            user = authService.login(request.getParameter("username"), request.getParameter("password"));
        } catch (InvalidUserCredentialsException | InvalidArgumentsException exception) {
            model.addAttribute("bodyContent", "login");
            model.addAttribute("hasError", true);
            model.addAttribute("error", exception.getMessage());
            return "accessDenied";
        }

        request.getSession().setAttribute("user", user);
        return "redirect:/home";
    }

}
