package pl.inz.praca.WsbPracaInz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.inz.praca.WsbPracaInz.auth.model.User;
import pl.inz.praca.WsbPracaInz.auth.service.UserService;
import pl.inz.praca.WsbPracaInz.model.BodySize;
import pl.inz.praca.WsbPracaInz.model.Exercises;
import pl.inz.praca.WsbPracaInz.model.Training;
import pl.inz.praca.WsbPracaInz.view.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getStats")
    public ResponseEntity<List<DashboardViewModel>> getStats(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).build();
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final List<DashboardViewModel> toReturn = new ArrayList<>();
        final List<Exercises> exercises = user.getExercises();
        if (exercises != null) {
            toReturn.add(new DashboardViewModel("Łącznie ćwiczeń", exercises.size(), exercises.stream().min((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).map(exp -> String.valueOf(exp.getCreatedAt())).orElse("Brak"), "fa-solid fa-dumbbell"));
        }
        final List<Training> trainings = user.getTrainings();
        if (trainings != null) {
            toReturn.add(new DashboardViewModel("Łącznie treningów", trainings.size(), trainings.stream().min((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).map(exp -> String.valueOf(exp.getCreatedAt())).orElse("Brak"), "fa-solid fa-person-hiking"));
        }
        final List<BodySize> bodySize = user.getBodySizes();
        if (trainings != null) {
            toReturn.add(new DashboardViewModel("Łącznie pomiarów ciała", bodySize.size(), bodySize.stream().min((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).map(exp -> String.valueOf(exp.getCreatedAt())).orElse("Brak"), "fa-solid fa-weight-scale"));
        }
        return ResponseEntity.ok(toReturn);
    }
}
