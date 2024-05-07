package com.example.sql_chatbot.Web;

import com.example.sql_chatbot.Models.Question;
import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Service.DatabaseService;
import com.example.sql_chatbot.Service.QuestionService;
import com.example.sql_chatbot.Models.Database;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Controller
public class QuestionController {


    private final QuestionService questionService;
    private final DatabaseService databaseService;

    public QuestionController(QuestionService questionService, DatabaseService databaseService) {
        this.questionService = questionService;
        this.databaseService = databaseService;
    }

    @Value("${flask.url}")
    private String FLASK_URL;

    @PostMapping("/chat/selectDatabase")
    public String selectDatabase(HttpSession session, @RequestParam Long databaseId, Model model) {
        Database selectedDatabase = this.databaseService.getDatabaseById(databaseId);
        if (selectedDatabase == null) {
            return "databaseNotFound";
        }
        Long isDatabase= databaseId;
        session.setAttribute("selectedDatabase", selectedDatabase);
        return "redirect:/chat";
    }

    @PostMapping("/ask_question")
    public String postQuestion(@RequestParam String question,
                               @AuthenticationPrincipal User user,
                               HttpSession session) throws JSONException {
        Database database = (Database) session.getAttribute("selectedDatabase");


        String username = database.getUsername();
        String password = database.getPassword();
        String host = database.getHost();
        String databaseName = database.getDatabaseName();
        String huggingface_token = user.getHuggingFaceAPIToken();

        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set form parameters
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("question", question);
        requestBody.add("dbUsername", username);
        requestBody.add("dbPass", password);
        requestBody.add("dbHost", host);
        requestBody.add("dbName", databaseName);
        requestBody.add("huggingface_token", huggingface_token);

        // Create HttpEntity with headers and form parameters
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request
        ResponseEntity<String> responseEntity = restTemplate.exchange(FLASK_URL, HttpMethod.POST, requestEntity, String.class);

        JSONObject jsonResponse = new JSONObject(responseEntity.getBody());
        String answer = jsonResponse.getString("answer");

        this.questionService.createQuestion(question,answer,database.getId());
        int firstAnswer = 1;
        return "redirect:/chat?firstAnswer=" + firstAnswer;
    }


    @GetMapping("/chat")
    public String getDataBases(Model model,
                               HttpSession session,
                               @AuthenticationPrincipal User user,
                               @RequestParam(required = false) Integer firstAnswer) {
        List<Database> databases = databaseService.findAllForUser(user.getUsername());
        Database selectedDatabase = (Database) session.getAttribute("selectedDatabase");
        List<Question> questions = new ArrayList<>();
        if(selectedDatabase!=null){
            questions = questionService.findAllForDatabase(selectedDatabase.getId());
        }

        Question lastQuestion = new Question();

        boolean isQuestions;
        if(questions.isEmpty()){
            isQuestions=true;
        }
        else {
            lastQuestion = questions.get(questions.size()-1);
            isQuestions=false;
        }


        model.addAttribute("databases", databases);
        model.addAttribute("questions", questions);

        model.addAttribute("selectedDatabase", selectedDatabase);
        model.addAttribute("isQuestions", isQuestions);

        model.addAttribute("firstAnswer",firstAnswer);
        model.addAttribute("lastQuestion",lastQuestion);
        return "chatting";
    }

}

