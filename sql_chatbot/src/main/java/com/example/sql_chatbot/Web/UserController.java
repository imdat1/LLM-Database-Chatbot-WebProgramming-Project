package com.example.sql_chatbot.Web;

import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.enumerations.Role;
import com.example.sql_chatbot.Models.exceptions.PasswordsDoNotMatchException;
import com.example.sql_chatbot.Service.DatabaseService;
import com.example.sql_chatbot.Service.QuestionService;
import com.example.sql_chatbot.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;
    private final RegisterController registerController;

    public UserController(UserService userService, QuestionService questionService, DatabaseService databaseService, RegisterController registerController) {
        this.userService = userService;
        this.registerController = registerController;
    }

    @GetMapping("/access_denied")
    public String AccessDenied(){
        return "accessDenied";
    }

    @GetMapping("/view_profile")
    public String ViewProfile(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("user",user);
        return "viewProfile";
    }

    @PostMapping("/edit_username")
    public String EditUsername(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("user",user);
        model.addAttribute("userUsername", user.getUsername());
        return "viewProfile";
    }
    @PostMapping("/edit_password")
    public String EditPassword(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("user",user);
        model.addAttribute("userPassword", true);
        return "viewProfile";
    }
    @PostMapping("/username_edited")
    public String UsernameEdited(@AuthenticationPrincipal User user, @RequestParam String newUsername, Model model, HttpSession session){
        try {
            this.userService.findByUsername(newUsername);
        }
        catch (UsernameNotFoundException exception){
            session.removeAttribute("sameUsernameError");
            user.setUsername(newUsername);
            this.userService.updateUser(user);
            model.addAttribute("user",user);
            return "redirect:/view_profile";
        }
        session.setAttribute("sameUsernameError", "A user with this username already exists!");
        return "forward:/edit_username";
    }
    @PostMapping("/password_edited")
    public String PasswordEdited(@AuthenticationPrincipal User user, @RequestParam String newPassword,
                                 @RequestParam String newRepeatedPassword, Model model,
                                 HttpSession session){
        try {
            this.userService.updateUserPassword(user.getUsername(),newPassword,newRepeatedPassword);
        }
        catch (PasswordsDoNotMatchException exception){
            if (!registerController.isValidPassword(newPassword)){
                session.setAttribute("passwordError", "Password must be at least 8 characters long, contain at least one uppercase letter, and have at least one symbol (!@#$%^&*)");
            }
            else {
                session.removeAttribute("passwordError");
            }
            session.setAttribute("exception", exception.getMessage());
            return "forward:/edit_password";
        }

        if(registerController.isValidPassword(newPassword)){
            session.removeAttribute("exception");
            session.removeAttribute("passwordError");
            model.addAttribute("user",user);
            return "redirect:/view_profile";
        }
        else {
            session.removeAttribute("exception");
            session.setAttribute("passwordError", "Password must be at least 8 characters long, contain at least one uppercase letter, and have at least one symbol (!@#$%^&*)");
            return "forward:/edit_password";
        }
    }

    @PostMapping("/edit_huggingfacetoken")
    public String EditHuggingFaceToken(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("user",user);
        model.addAttribute("userHuggingFaceToken", user.getHuggingFaceAPIToken());
        return "viewProfile";
    }

    @PostMapping("/huggingfacetoken_edited")
    public String HuggingFaceTokenEdited(@AuthenticationPrincipal User user, Model model, @RequestParam String newHuggingFaceToken, HttpSession session){

        if(!registerController.isValidHuggingFaceToken(newHuggingFaceToken)){
            session.setAttribute("huggingFaceTokenError", "Your HuggingFace API Token is Invalid!");
            return "forward:/edit_huggingfacetoken";
        };
        session.removeAttribute("huggingFaceTokenError");
        model.addAttribute("user", user);
        this.userService.updateUserCredentials(user.getUsername(), newHuggingFaceToken, Role.ROLE_USER);
        return "viewProfile";
    }
}
