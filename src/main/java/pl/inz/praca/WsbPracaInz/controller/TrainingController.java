package pl.inz.praca.WsbPracaInz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.inz.praca.WsbPracaInz.auth.model.User;
import pl.inz.praca.WsbPracaInz.auth.service.UserService;
import pl.inz.praca.WsbPracaInz.model.Training;
import pl.inz.praca.WsbPracaInz.request.TrainingRequest;
import pl.inz.praca.WsbPracaInz.services.TrainingService;
import pl.inz.praca.WsbPracaInz.view.TrainingViewModel;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
public class TrainingController {


    private final TrainingService trainingService;
    private final UserService userService;

    public TrainingController(TrainingService trainingService, UserService userService) {
        this.trainingService = trainingService;
        this.userService = userService;
    }

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<?> addTraining(Authentication authentication, @RequestBody TrainingRequest dto) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        return this.trainingService.addTraining(authentication.getName(), dto);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getTraining( Authentication authentication, @Valid @PathVariable Long id) {
        return this.trainingService.getTraining(authentication, id);
    }
    @PostMapping("/update")
    @Transactional
    public ResponseEntity<?> updateTraining(@RequestBody @Valid TrainingRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        return this.trainingService.editTraining(authentication.getName(), request);
    }

    @PostMapping("/deleteTraining")
    @Transactional
    public ResponseEntity<?> deleteTraining(@RequestBody String json, Authentication authentication) throws JSONException {
        return this.trainingService.deleteTraining(json,authentication);
    }
    @GetMapping("/getList")
    public ResponseEntity<?> getListTrainings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.getTrainings());
    }


    @PostMapping("/getList")
    @Transactional
    public ResponseEntity<?> getListTrainings(@RequestBody String json, Authentication authentication) throws JSONException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final JSONObject jsonObject = new JSONObject(json);
        final int currentPage = jsonObject.getInt("page");
        final String order = jsonObject.getString("order");
        final boolean sort = jsonObject.getBoolean("sort");
        final String search = (jsonObject.isNull("search") ? null : jsonObject.getString("search"));


        final Map<String,Object> toReturn = new HashMap<>();
        List<Training> trainings = user.getTrainings();
        if (search != null && !search.equalsIgnoreCase("null")) {
            trainings = trainings.stream().filter(exp ->exp.getExercises().getName().matches("(.*)"+search+"(.*)")).toList();
        }
        if (order != null && order.equalsIgnoreCase("created_at")) {
            trainings = trainings.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }).toList();
        }
        else if (order != null && order.equalsIgnoreCase("updated_at")) {
            trainings = trainings.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getUpdatedAt().compareTo(o1.getUpdatedAt());
                }
                return o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
            }).toList();
        }
        else if (order != null && order.equalsIgnoreCase("name")) {
            trainings = trainings.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getExercises().getName().compareTo(o1.getExercises().getName());
                }
                return o1.getExercises().getName().compareTo(o2.getExercises().getName());
            }).toList();
        }
        final double SIZE = 10;
        int from;
        if(currentPage == 1) {
            from = 0;
        } else {
            from = (int) ((currentPage - 1) * SIZE);
        }
        int to = (int) (from + SIZE);
        if(to > trainings.size()) {
            to = trainings.size();
        }
        if(from > trainings.size()) {
            toReturn.put("totalPages", 1);
            toReturn.put("data", new ArrayList<>());
            return ResponseEntity.ok(toReturn);
        }
        int totalPages = (int) (Math.ceil(trainings.size() / SIZE)  );
        if (currentPage > totalPages) {
            toReturn.put("totalPages", 1);
            toReturn.put("data", new ArrayList<>());
            return ResponseEntity.ok(toReturn);
        }
        toReturn.put("totalPages", totalPages);
        toReturn.put("data",  trainings.subList(from,to).stream().map(TrainingViewModel::new).toList());
        return ResponseEntity.ok(toReturn);
    }

    @PostMapping("/getAnalyze/{type}")
    public ResponseEntity<?> getAnylize(@PathVariable String type, @RequestBody String json, Authentication authentication) throws JSONException, JsonProcessingException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final JSONObject jsonObject = new JSONObject(json);
        final List<String> exercise = (jsonObject.isNull("exercises") ? new ArrayList<>() :jsonObject.getJSONArray("exercises").toList().stream().map(o -> (String)o).toList());
        return this.trainingService.getAnalyze(authentication, type,exercise);
    }
}
