package pl.inz.praca.WsbPracaInz.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.inz.praca.WsbPracaInz.auth.model.User;
import pl.inz.praca.WsbPracaInz.auth.service.UserService;
import pl.inz.praca.WsbPracaInz.model.Exercises;
import pl.inz.praca.WsbPracaInz.repo.ExercisesRepo;

import java.util.List;

@Service
public class ExercisesService {

    private final ExercisesRepo exercisesRepo;
    private final UserService userService;

    public ExercisesService(ExercisesRepo exercisesRepo, UserService userService) {
        this.exercisesRepo = exercisesRepo;
        this.userService = userService;
    }

    public void save(Exercises toSave) {
        this.exercisesRepo.save(toSave);
    }

    public void addExercises(final Exercises exercises, User user) {
        if (user == null) {
            return;
        }
        user.addExercises(this.exercisesRepo.save(exercises));
        this.userService.saveUser(user);
    }

    public List<Exercises> getAll() {
        return this.exercisesRepo.findAll();
    }

    public List<Exercises> getAll(PageRequest pageRequest) {
        return this.exercisesRepo.findAllBy(pageRequest);
    }

    public void delete(Long id) {
        this.exercisesRepo.removeById(id);
    }
}
