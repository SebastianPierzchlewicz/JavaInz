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
import pl.inz.praca.WsbPracaInz.model.BodySize;
import pl.inz.praca.WsbPracaInz.request.BodySizeRequest;
import pl.inz.praca.WsbPracaInz.services.BodySizeService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bodySize")
@Slf4j
public class BodySizeController {

    private final UserService userService;
    private final BodySizeService bodySizeService;

    public BodySizeController(UserService userService, BodySizeService bodySizeService) {
        this.userService = userService;
        this.bodySizeService = bodySizeService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBodySize(@RequestBody @Valid BodySizeRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final var bodySize = new BodySize(request);
        this.bodySizeService.addBodySize(bodySize, user);
        return ResponseEntity.ok(bodySize.getId());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getBodySize(Authentication authentication, @Valid @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final BodySize bodySize = user.getBodySize(id);
        if (bodySize == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bodySize);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateBodySize(@RequestBody @Valid BodySizeRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final BodySize toSave = user.getBodySize(request.getId());
        if (toSave == null) {
            return ResponseEntity.notFound().build();
        }
        toSave.setChest(request.getChest());
        toSave.setNeck(request.getNeck());
        toSave.setBiceps(request.getBiceps());
        toSave.setWaist(request.getWaist());
        toSave.setBelt(request.getBelt());
        toSave.setHip(request.getHip());
        toSave.setThigh(request.getThigh());
        toSave.setCalf(request.getCalf());
        toSave.setWeight(request.getWeight());
        toSave.setHeight(request.getHeight());
        toSave.setUpdatedAt(LocalDateTime.now());
        this.bodySizeService.save(toSave);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deleteBodySize")
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
        final BodySize toRemove = user.getBodySize(id);
        if (toRemove == null) {
            return ResponseEntity.notFound().build();
        }
        user.removeBodySize(toRemove);
        userService.saveUser(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getList")
    public ResponseEntity<?> getListBodySize(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.getBodySizes());
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

        final Map<String, Object> toReturn = new HashMap<>();
        List<BodySize> exercises = user.getBodySizes();
        if (search != null && !search.equalsIgnoreCase("null")) {
            exercises = exercises.stream().filter(exp ->exp.getCreatedAt().toString().matches("(.*)"+search+"(.*)")).toList();
        }
        if (order != null && order.equalsIgnoreCase("created_at")) {
            exercises = exercises.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }).toList();
        } else if (order != null && order.equalsIgnoreCase("updated_at")) {
            exercises = exercises.stream().sorted((o1, o2) -> {
                if (sort) {
                    return o2.getUpdatedAt().compareTo(o1.getUpdatedAt());
                }
                return o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
            }).toList();
        }
        final double SIZE = 10;
        int from;
        if (currentPage == 1) {
            from = 0;
        } else {
            from = (int) ((currentPage - 1) * SIZE);
        }
        int to = (int) (from + SIZE);
        if (to > exercises.size()) {
            to = exercises.size();
        }
        if (from > exercises.size()) {
            toReturn.put("totalPages", 1);
            toReturn.put("data", new ArrayList<>());
            return ResponseEntity.ok(toReturn);
        }
        int totalPages = (int) (Math.ceil(exercises.size() / SIZE));
        if (currentPage > totalPages) {
            toReturn.put("totalPages", 1);
            toReturn.put("data", new ArrayList<>());
            return ResponseEntity.ok(toReturn);
        }
        toReturn.put("totalPages", totalPages);
        toReturn.put("data", exercises.subList(from, to));
        return ResponseEntity.ok(toReturn);
    }

    @PostMapping("/getAnalyze/{type}")
    public ResponseEntity<?> getAnylize(@PathVariable String type, Authentication authentication) throws JSONException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return this.bodySizeService.getAnalyze(authentication, type);
    }
}
