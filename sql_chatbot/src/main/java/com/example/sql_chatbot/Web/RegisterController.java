package com.example.sql_chatbot.Web;

import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.enumerations.Role;
import com.example.sql_chatbot.Models.exceptions.InvalidArgumentsException;
import com.example.sql_chatbot.Models.exceptions.PasswordsDoNotMatchException;
import com.example.sql_chatbot.Models.exceptions.UsernameAlreadyExistsException;
import com.example.sql_chatbot.Service.AuthService;
import com.example.sql_chatbot.Service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private final UserService userService;

    private final AuthService authService;

    @Value("${huggingface.token.url}")
    private String HUGGINGFACE_URL;

    public RegisterController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public String getRegisterPage(@RequestParam(required = false) String error, Model model) {
        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        return "register";
    }

    @PostMapping
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String repeatedPassword,
                           @RequestParam String huggingFaceAPIToken,
                           Model model) {
        try{
            Role role1 = Role.ROLE_USER;
            if(!isValidHuggingFaceToken(huggingFaceAPIToken) && !isValidPassword(password)){
                model.addAttribute("userErrorUsername",username);
                model.addAttribute("passwordError", "Password must be at least 8 characters long, contain at least one uppercase letter, and have at least one symbol (!@#$%^&*)");
                model.addAttribute("huggingFaceTokenError", "Invalid HuggingFace API token. Try entering your HuggingFace API token again!");
                return "register"; // Return the registration form with an error message
            }
            if (!isValidPassword(password)) {

                model.addAttribute("passwordError", "Password must be at least 8 characters long, contain at least one uppercase letter, and have at least one symbol (!@#$%^&*)");
                model.addAttribute("userErrorUsername",username);
                model.addAttribute("userErrorHuggingFace",huggingFaceAPIToken);
                return "register"; // Return the registration form with an error message
            }

            if (!isValidHuggingFaceToken(huggingFaceAPIToken)) {

                model.addAttribute("huggingFaceTokenError", "Invalid HuggingFace API token. Try entering your HuggingFace API token again!");
                model.addAttribute("userErrorUsername",username);
                model.addAttribute("userErrorPassword",password);
                return "register"; // Return the registration form with an error message
            }


            this.userService.register(username, password, repeatedPassword, huggingFaceAPIToken, role1);
            return "redirect:/login";

        } catch (InvalidArgumentsException | PasswordsDoNotMatchException | UsernameAlreadyExistsException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$");
    }

    public boolean isValidHuggingFaceToken(String token) {
        String apiUrl = HUGGINGFACE_URL;
        String authHeader = "Bearer " + token;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        }
        catch (HttpClientErrorException exception){
            return false;
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        // Check if the response status code indicates success
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            // Check if the response contains user information
            if (responseBody != null && responseBody.contains("\"type\":\"user\"")) {
                return true; // Token is valid
            } else if (responseBody != null && responseBody.contains("Invalid username or password.")) {
                return false; // Invalid username or password, token is invalid
            } else {
                // Unknown response, consider token as invalid
                return false;
            }
        } else {
            return false; // Request failed, token is considered invalid
        }
    }
}
