package com.example.sql_chatbot.Web;

import com.example.sql_chatbot.Models.Database;
import com.example.sql_chatbot.Models.Question;
import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Service.DatabaseService;
import com.example.sql_chatbot.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.NonUniqueResultException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DatabaseController {

    private final DatabaseService databaseService;

    private final UserService userService;

    public DatabaseController(DatabaseService databaseService, UserService userService) {
        this.databaseService = databaseService;
        this.userService = userService;
    }

    @Value("${flask.url2}")
    private String FLASK_URL2;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/add_database")
    public String showAddDatabaseForm(Model model) {
        return "addDatabase";
    }

    @PostMapping("/add_database/")
    public String addDatabase(@RequestParam String name,
                              @RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String host,
                              @RequestParam String databaseName,
                              @AuthenticationPrincipal User user,
                              Model model,
                              HttpServletRequest request) throws JSONException {
            String status = checkDatabaseCredentials(username,password,host,databaseName);

            if ("success".equals(status)) {
                Database database = databaseService.createDatabase(name, username, password, host, databaseName, user.getUsername());
                request.getSession().setAttribute("selectedDatabase", database);
                return "redirect:/chat";
            } else {
                Database database = new Database(name, username, password, host, databaseName, user);
                boolean noSuccess = true;

                model.addAttribute("noSuccess", noSuccess);
                model.addAttribute("database", database);
                return "addDatabase.html";
            }
    }

    @GetMapping("/edit_database")
    public String editDatabase(Model model, HttpServletRequest request, @RequestParam(required = false) Long adminEditing){
        Database database = (Database) request.getSession().getAttribute("selectedDatabase");
        request.getSession().setAttribute("adminEditing",adminEditing);
        model.addAttribute("database", database);

        Boolean noSuccess = (Boolean) request.getSession().getAttribute("noSuccessAdmin");
        if(noSuccess!=null){
            request.getSession().removeAttribute("noSuccessAdmin");
            noSuccess = true;
        }

        model.addAttribute("noSuccess",noSuccess);
        return "addDatabase";
    }

    @PostMapping("/add_database/{id}")
    public String editDatabase(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String host,
                               @RequestParam String databaseName,
                               @AuthenticationPrincipal User user,
                               HttpServletRequest request) throws JSONException{
        Long adminEditing = (Long) request.getSession().getAttribute("adminEditing");
        if(adminEditing!=null){
            Database database = this.databaseService.getDatabaseById(id);
            String databaseUsername = database.getUser().getUsername();
            String status = checkDatabaseCredentials(username,password,host,databaseName);
            if ("success".equals(status)) {
                this.databaseService.update(id, name, username, password, host, databaseName, databaseUsername);
                request.getSession().removeAttribute("selectedDatabase");
                request.getSession().removeAttribute("adminEditing");
                return "redirect:/admin/all_databases";
            } else {
                boolean noSuccess = true;
                request.getSession().setAttribute("noSuccessAdmin", noSuccess);
                return "redirect:/edit_database?adminEditing=1";
            }
        }

        String status = checkDatabaseCredentials(username,password,host,databaseName);
        if("success".equals(status)){
            this.databaseService.update(id, name, username, password, host, databaseName, user.getUsername());
            Database database = this.databaseService.getDatabaseById(id);
            request.getSession().setAttribute("selectedDatabase", database);
            return "redirect:/chat";
        }
        boolean noSuccess = true;
        request.getSession().setAttribute("noSuccessAdmin", noSuccess);
        return "redirect:/edit_database";
    }

    @PostMapping("/delete_database/{id}")
    public String deleteDatabase(@PathVariable Long id, HttpServletRequest request){

        request.getSession().removeAttribute("selectedDatabase");
        this.databaseService.deleteDatabase(id);
        return "redirect:/chat";
    }

    private String checkDatabaseCredentials(String username,
                                                            String password,
                                                            String host,
                                                            String databaseName) throws JSONException{
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set form parameters
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("dbUsername", username);
        requestBody.add("dbPass", password);
        requestBody.add("dbHost", host);
        requestBody.add("dbName", databaseName);

        // Create HttpEntity with headers and form parameters
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request
        ResponseEntity<String> responseEntity = restTemplate.exchange(FLASK_URL2, HttpMethod.POST, requestEntity, String.class);
        JSONObject jsonResponse = new JSONObject(responseEntity.getBody());
        String status = jsonResponse.getString("status");
        return status;
    }

}

