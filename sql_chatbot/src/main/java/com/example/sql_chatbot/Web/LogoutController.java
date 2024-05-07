package com.example.sql_chatbot.Web;

import com.example.sql_chatbot.Models.enumerations.Role;
import com.example.sql_chatbot.Models.exceptions.InvalidArgumentsException;
import com.example.sql_chatbot.Models.exceptions.PasswordsDoNotMatchException;
import com.example.sql_chatbot.Models.exceptions.UsernameAlreadyExistsException;
import com.example.sql_chatbot.Service.AuthService;
import com.example.sql_chatbot.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/logout")
public class LogoutController {
    @GetMapping
    public String logout(HttpServletRequest request, Model model) {
        request.getSession().invalidate();
        return "redirect:/login";
    }

}


