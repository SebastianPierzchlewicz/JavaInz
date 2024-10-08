package pl.inz.praca.WsbPracaInz.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.inz.praca.WsbPracaInz.auth.model.User;
import pl.inz.praca.WsbPracaInz.auth.service.UserService;
import pl.inz.praca.WsbPracaInz.model.Exercises;
import pl.inz.praca.WsbPracaInz.request.ExercisesRequest;
import pl.inz.praca.WsbPracaInz.services.ExercisesService;
import pl.inz.praca.WsbPracaInz.services.TrainingService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/api/exercises")
@Slf4j
public class ExercisesController {

    private final UserService userService;
    private final ExercisesService exercisesService;
    private final TrainingService trainingService;

    public ExercisesController(UserService userService, ExercisesService exercisesService, TrainingService trainingService) {
        this.userService = userService;
        this.exercisesService = exercisesService;
        this.trainingService = trainingService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addExercise(@RequestBody @Valid ExercisesRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getExerciseByName(request.getName()) != null) {
            return ResponseEntity.status(500).body("Ćwiczenie o nazwie " + request.getName() +" już istnieje!");
        }
        final var exe = new Exercises(request);
        this.exercisesService.addExercises(exe,user);
        return ResponseEntity.ok(exe.getId());
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getExercise( Authentication authentication, @Valid @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final Exercises exercise = user.getExercise(id);
        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercise);
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateExercise(@RequestBody @Valid ExercisesRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final Exercises fromUser = user.getExerciseByName(request.getName());
        if (fromUser != null && !fromUser.getId().equals(request.getId())) {
            return ResponseEntity.status(500).body("Ćwiczenie o nazwie " + request.getName() +" już istnieje!");
        }
        final Exercises toSave = user.getExercise(request.getId());
        if (toSave == null) {
            return ResponseEntity.notFound().build();
        }
        toSave.setName(request.getName());
        toSave.setDifficulty(request.getDifficulty());
        toSave.setSeriesAmount(request.getSeriesAmount());
        toSave.setUpdatedAt(LocalDateTime.now());
        this.exercisesService.save(toSave);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deleteExercise")
    @Transactional
    public ResponseEntity<?> deleteExercise(@RequestBody String json, Authentication authentication) throws JSONException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final JSONObject object = new JSONObject(json);

        final long id = object.getLong("id");
        final Exercises toRemove = user.getExercise(id);
        if (toRemove == null) {
            return ResponseEntity.notFound().build();
        }
        user.getTrainings().stream().filter(training -> training.getExercises().getId().equals(toRemove.getId())).forEach(training -> {
            trainingService.deleteById(training.getId());
        });
        user.getTrainings().removeIf(training -> training.getExercises().getId().equals(toRemove.getId()));
        user.removeExercises(toRemove);
        userService.saveUser(user);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/getList")
    public ResponseEntity<?> getListExercises(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.getExercises());
    }


    @PostMapping("/getList")
    public ResponseEntity<?> getListExercises(@RequestBody String json, Authentication authentication) throws JSONException {
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
         List<Exercises> exercises = user.getExercises();
         if (search != null && !search.equalsIgnoreCase("null")) {
             exercises = exercises.stream().filter(exp ->exp.getName().matches("(.*)"+search+"(.*)")).toList();
         }
        if (order != null && order.equalsIgnoreCase("created_at")) {
            exercises = exercises.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }).toList();
        }
        else if (order != null && order.equalsIgnoreCase("updated_at")) {
            exercises = exercises.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getUpdatedAt().compareTo(o1.getUpdatedAt());
                }
                return o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
            }).toList();
        }
        else if (order != null && order.equalsIgnoreCase("name")) {
            exercises = exercises.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getName().compareTo(o1.getName());
                }
                return o1.getName().compareTo(o2.getName());
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
        if(to > exercises.size()) {
            to = exercises.size();
        }
        if(from > exercises.size()) {
            toReturn.put("totalPages", 1);
            toReturn.put("data", new ArrayList<>());
            return ResponseEntity.ok(toReturn);
        }
        int totalPages = (int) (Math.ceil(exercises.size() / SIZE)  );
        if (currentPage > totalPages) {
            toReturn.put("totalPages", 1);
            toReturn.put("data", new ArrayList<>());
            return ResponseEntity.ok(toReturn);
        }
        toReturn.put("totalPages", totalPages);
        toReturn.put("data", exercises.subList(from,to));
        return ResponseEntity.ok(toReturn);
    }
}
