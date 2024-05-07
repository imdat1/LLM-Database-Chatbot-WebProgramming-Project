package com.example.sql_chatbot.Web;

import com.example.sql_chatbot.Models.Database;
import com.example.sql_chatbot.Models.Question;
import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.enumerations.Role;
import com.example.sql_chatbot.Models.exceptions.InvalidArgumentsException;
import com.example.sql_chatbot.Models.exceptions.PasswordsDoNotMatchException;
import com.example.sql_chatbot.Models.exceptions.UsernameAlreadyExistsException;
import com.example.sql_chatbot.Service.DatabaseService;
import com.example.sql_chatbot.Service.QuestionService;
import com.example.sql_chatbot.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final DatabaseService databaseService;
    private final QuestionService questionService;
    private final UserService userService;
    private final RegisterController registerController;

    public AdminController(DatabaseService databaseService, QuestionService questionService, UserService userService, RegisterController registerController) {
        this.databaseService = databaseService;
        this.questionService = questionService;
        this.userService = userService;
        this.registerController = registerController;
    }

    @GetMapping("/all_databases")
    public String AdminDatabases(@AuthenticationPrincipal User user, Model model){
        if(user.getRole().equals(Role.ROLE_USER)) {
            System.out.println(user.getRole());
            return "accessDenied";}

        List<Database> databases = this.databaseService.findAll();
        model.addAttribute("databases",databases);
        return "adminDatabases";
    }

    @GetMapping("/database_questions")
    public String GetDatabaseQuestions(HttpSession session, Model model){
        Database database= (Database) session.getAttribute("adminDatabase");
        List<Question> questions = this.questionService.findAllForDatabase(database.getId());

        model.addAttribute("adminDatabase", database);
        model.addAttribute("adminQuestions", questions);
        return "adminChat";
    }

    @GetMapping("/all_users")
    public String AllUsers(Model model, @AuthenticationPrincipal User user){
        List<User> users = this.userService.findAll();
        model.addAttribute("users",users);
        model.addAttribute("loggedInUser", user);
        return "adminUsers";
    }

    @GetMapping("/edit_user")
    public String EditUser(@RequestParam(required = false) String error, HttpSession session, Model model){

        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        User user = (User) session.getAttribute("userForEditing");
        model.addAttribute("userErrorUsername",user.getUsername());
        model.addAttribute("userErrorHuggingFace",user.getHuggingFaceAPIToken());
        model.addAttribute("userErrorPassword",user.getPassword());
        model.addAttribute("userRole",user.getRole());
        System.out.println(user.getRole());
        return "adminEditUser";
    }

    @PostMapping("/database_questions")
    public String DatabaseQuestions(HttpSession session, @RequestParam Long databaseId) {
        if (databaseId == null) {
            return "databaseNotFound";
        }
        Database database = this.databaseService.getDatabaseById(databaseId);

        session.setAttribute("adminDatabase", database);
        return "redirect:/admin/database_questions";
    }
    @PostMapping("/edit_database")
    public String DatabaseEdit(HttpSession session, @RequestParam Long databaseId) {
        if (databaseId == null) {
            return "databaseNotFound";
        }

        Database database = this.databaseService.getDatabaseById(databaseId);
        session.setAttribute("selectedDatabase", database);

        return "redirect:/edit_database?adminEditing=1";
    }

    @PostMapping("/delete_user")
    public String DeleteUser(@RequestParam String username){
        User user = this.userService.findByUsername(username);
        this.userService.deleteUser(user);
        return "redirect:/admin/all_users";
    }

    @PostMapping("/delete_database/{id}")
    public String deleteDatabase(@PathVariable Long id){
        this.databaseService.deleteDatabase(id);
        return "redirect:/admin/all_databases";
    }

    @PostMapping("/edit_user")
    public String EditUser(@RequestParam String username, HttpSession session){
        User user = this.userService.findByUsername(username);
        session.setAttribute("userForEditing", user);
        return "redirect:/admin/edit_user";
    }

    @PostMapping("/edit_user_credentials")
    public String EditUserCredentials(@RequestParam String username,
                             @RequestParam String huggingFaceAPIToken,
                             @RequestParam Role role,
                             HttpSession session,
                             Model model){

        try{
            if (!registerController.isValidHuggingFaceToken(huggingFaceAPIToken)) {

                model.addAttribute("huggingFaceTokenError", "Invalid HuggingFace API token. Try entering your HuggingFace API token again!");
                model.addAttribute("userErrorUsername",username);
                return "adminEditUser"; // Return the registration form with an error message
            }


            User user = (User) session.getAttribute("userForEditing");
            this.userService.updateUsername(user.getUsername(),username);
            this.userService.updateUserCredentials(username,huggingFaceAPIToken,role);

            return "redirect:/admin/all_users";

        } catch (InvalidArgumentsException | PasswordsDoNotMatchException | UsernameAlreadyExistsException exception) {
            return "redirect:/admin/edit_user?error=" + exception.getMessage();
        }
    }

    @PostMapping("/edit_user_password")
    public String EditUserPassword(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String repeatedPassword,
                                   Model model){
        try {
            if (!registerController.isValidPassword(password)) {
                model.addAttribute("passwordError", "Password must be at least 8 characters long, contain at least one uppercase letter, and have at least one symbol (!@#$%^&*)");
                return "adminEditUser"; // Return the registration form with an error message
            }
            this.userService.updateUserPassword(username,password,repeatedPassword);
            return "redirect:/admin/all_users";

        }
        catch (InvalidArgumentsException | PasswordsDoNotMatchException | UsernameAlreadyExistsException exception){
            return "redirect:/admin/edit_user?error=" + exception.getMessage();
        }
    }

    @PostMapping("/lock_user")
    public String LockUser(@RequestParam String username){
        User user = this.userService.findByUsername(username);
        user.setAccountNonLocked(false);
        this.userService.updateUser(user);
        return "redirect:/admin/all_users";
    }
    @PostMapping("/unlock_user")
    public String UnlockUser(@RequestParam String username){
        User user = this.userService.findByUsername(username);
        user.setAccountNonLocked(true);
        this.userService.updateUser(user);
        return "redirect:/admin/all_users";
    }

}
