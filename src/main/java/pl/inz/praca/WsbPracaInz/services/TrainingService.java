package pl.inz.praca.WsbPracaInz.services;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.inz.praca.WsbPracaInz.auth.model.User;
import pl.inz.praca.WsbPracaInz.auth.service.UserService;
import pl.inz.praca.WsbPracaInz.helper.MathHelper;
import pl.inz.praca.WsbPracaInz.model.Exercises;
import pl.inz.praca.WsbPracaInz.model.Series;
import pl.inz.praca.WsbPracaInz.model.Training;
import pl.inz.praca.WsbPracaInz.repo.SeriesRepo;
import pl.inz.praca.WsbPracaInz.repo.TrainingRepo;
import pl.inz.praca.WsbPracaInz.request.TrainingRequest;
import pl.inz.praca.WsbPracaInz.view.TrainingGetViewModel;
import pl.inz.praca.WsbPracaInz.view.analyze.AnalyzeViewModel;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
public class TrainingService {

    private final TrainingRepo trainingRepo;
    private final UserService userService;
    private final SeriesRepo seriesRepo;

    public TrainingService(TrainingRepo trainingRepo, UserService userService, SeriesRepo seriesRepo) {
        this.trainingRepo = trainingRepo;
        this.userService = userService;
        this.seriesRepo = seriesRepo;
    }

    public void removeSeries(final long id) {
        this.seriesRepo.deleteById(id);
    }

    public void addSeries(final Series series) {
        this.seriesRepo.save(series);
    }

    public void save(Training training) {
        this.trainingRepo.save(training);
    }

    public ResponseEntity<?> addTraining(final String username, final TrainingRequest dto) {
        final User user = this.userService.getUser(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final Exercises exercises = user.getExercise(dto.getExerciseId());
        if (exercises == null) {
            return ResponseEntity.notFound().build();
        }
        final List<Series> seriesList = dto.getSeries().stream().map(Series::new).toList();
        seriesList.forEach(this.seriesRepo::save);
        final Training training = new Training(dto.getDifficulty(), exercises, seriesList);
        this.trainingRepo.save(training);
        user.addTraining(training);
        userService.saveUser(user);
        return ResponseEntity.ok(training.getId());

    }

    public ResponseEntity<?> editTraining(final String username, final TrainingRequest request) {
        final User user = this.userService.getUser(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final Exercises exercises = user.getExercise(request.getExerciseId());
        if (exercises == null) {
            return ResponseEntity.notFound().build();
        }
        final Training old = trainingRepo.findById(request.getId()).orElse(null);
        if (old == null) {
            return ResponseEntity.notFound().build();
        }
        old.getSeries().forEach(series -> this.removeSeries(series.getId()));
        final List<Series> seriesList = request.getSeries().stream().map(Series::new).toList();
        seriesList.forEach(this.seriesRepo::save);
        final Training training = new Training(request.getId(), request.getDifficulty(), exercises, seriesList, old.getCreatedAt(), null);
        this.trainingRepo.save(training);
        user.removeTraining(request.getId());
        user.addTraining(training);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> getTraining(Authentication authentication, Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final Training training = user.getTraining(id);
        if (training == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new TrainingGetViewModel(training));
    }

    public void deleteById(final Long id) {
        this.trainingRepo.deleteById(id);
    }
    public ResponseEntity<?> deleteTraining(String json, Authentication authentication) throws JSONException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        final JSONObject object = new JSONObject(json);

        final long id = object.getLong("id");
        final Training toRemove = this.trainingRepo.findById(id).orElse(null);
        if (toRemove == null) {
            return ResponseEntity.notFound().build();
        }
        toRemove.getSeries().forEach(series -> this.removeSeries(series.getId()));
        user.removeTraining(toRemove);
        userService.saveUser(user);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> getAnalyze(final Authentication authentication, final String type, final List<String> exercises) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getTrainings().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        switch (type.toLowerCase()) {
            case "tydzien" -> {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusDays(7).withHour(23).withMinute(59).withSecond(59);
                final List<Training> value = user.getTrainings().stream().filter(training -> exercises.contains(training.getExercises().getName())).filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(Training::getCreatedAt)).toList();
                exercises.forEach(name -> {
                    final Training first = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted(Comparator.comparing(Training::getCreatedAt)).findFirst().orElse(null);
                    final Training last = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                    if (first == null || last == null) {
                        return;
                    }
                    analyzeViewModel.addProgress(name, first.countPowerProgress(), last.countPowerProgress(), true, "kg/s");
                });
                IntStream.range(0, 8).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusDays(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusDays(i);
                    final List<Training> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    exercises.forEach(name -> {
                        final var training = sub.stream().filter(t -> t.getExercises().getName().equalsIgnoreCase(name)).toList();
                        if (training.isEmpty()) {
                            return;
                        }
                        analyzeViewModel.getCanvas().addLabel(finalSubTimeStart.toLocalDate().toString());
                        analyzeViewModel.getCanvas().addValue(name, training.stream().map(Training::countPowerProgress).reduce(0.0, Double::sum) / training.size());
                    });
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            case "miesiac" -> {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusWeeks(5).withHour(0).withMinute(0).withSecond(0).with(DayOfWeek.MONDAY);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusWeeks(5).withHour(23).withMinute(59).withSecond(59).with(DayOfWeek.SUNDAY);
                final List<Training> value = user.getTrainings().stream().filter(training -> exercises.contains(training.getExercises().getName())).filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(Training::getCreatedAt)).toList();
                exercises.forEach(name -> {
                    final Training first = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted(Comparator.comparing(Training::getCreatedAt)).findFirst().orElse(null);
                    final Training last = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                    if (first == null || last == null) {
                        return;
                    }
                    analyzeViewModel.addProgress(name, first.countPowerProgress(), last.countPowerProgress(), true, "kg/s");
                });
                IntStream.range(0, 6).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusWeeks(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusWeeks(i);
                    final List<Training> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    exercises.forEach(name -> {
                        final var training = sub.stream().filter(t -> t.getExercises().getName().equalsIgnoreCase(name)).toList();
                        if (training.isEmpty()) {
                            return;
                        }
                        analyzeViewModel.getCanvas().addLabel(this.buildDate(finalSubTimeStart,finalSubTimeEnd));
                        analyzeViewModel.getCanvas().addValue(name, training.stream().map(Training::countPowerProgress).reduce(0.0, Double::sum) / training.size());
                    });
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            case "3miesiace" -> {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusWeeks(15).withHour(0).withMinute(0).withSecond(0).with(DayOfWeek.MONDAY);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusWeeks(15).withHour(23).withMinute(59).withSecond(59).with(DayOfWeek.SUNDAY);
                final List<Training> value = user.getTrainings().stream().filter(training -> exercises.contains(training.getExercises().getName())).filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(Training::getCreatedAt)).toList();
                exercises.forEach(name -> {
                    final Training first = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted(Comparator.comparing(Training::getCreatedAt)).findFirst().orElse(null);
                    final Training last = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                    if (first == null || last == null) {
                        return;
                    }
                    analyzeViewModel.addProgress(name, first.countPowerProgress(), last.countPowerProgress(), true, "kg/s");
                });
                IntStream.range(0, 16).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusWeeks(i - 1);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusWeeks(i);
                    final List<Training> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    exercises.forEach(name -> {
                        final var training = sub.stream().filter(t -> t.getExercises().getName().equalsIgnoreCase(name)).toList();
                        if (training.isEmpty()) {
                            return;
                        }
                        analyzeViewModel.getCanvas().addLabel(this.buildDate(finalSubTimeStart,finalSubTimeEnd));
                        analyzeViewModel.getCanvas().addValue(name, training.stream().map(Training::countPowerProgress).reduce(0.0, Double::sum) / training.size());
                    });
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            case "6miesiecy" -> {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusMonths(6).with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
                final List<Training> value = user.getTrainings().stream().filter(training -> exercises.contains(training.getExercises().getName())).filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(Training::getCreatedAt)).toList();
                exercises.forEach(name -> {
                    final Training first = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted(Comparator.comparing(Training::getCreatedAt)).findFirst().orElse(null);
                    final Training last = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                    if (first == null || last == null) {
                        return;
                    }
                    analyzeViewModel.addProgress(name, first.countPowerProgress(), last.countPowerProgress(), true, "kg/s");
                });
                IntStream.range(0, 7).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusMonths(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusMonths(i);

                    final List<Training> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    exercises.forEach(name -> {
                        final var training = sub.stream().filter(t -> t.getExercises().getName().equalsIgnoreCase(name)).toList();
                        if (training.isEmpty()) {
                            return;
                        }
                        String month = finalSubTimeStart.getMonthValue() + "";
                        String year = finalSubTimeStart.getYear() + "";
                        if (finalSubTimeStart.getMonthValue() < 10) {
                            month = "0" + month;
                        }
                        analyzeViewModel.getCanvas().addLabel(year + "-" + month);

                        analyzeViewModel.getCanvas().addValue(name, training.stream().map(Training::countPowerProgress).reduce(0.0, Double::sum) / training.size());
                    });
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            case "rok" -> {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusMonths(12).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusMonths(12).with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
                final List<Training> value = user.getTrainings().stream().filter(training -> exercises.contains(training.getExercises().getName())).filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(Training::getCreatedAt)).toList();
                exercises.forEach(name -> {
                    final Training first = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted(Comparator.comparing(Training::getCreatedAt)).findFirst().orElse(null);
                    final Training last = value.stream().filter(training -> training.getExercises().getName().equalsIgnoreCase(name)).sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                    if (first == null || last == null) {
                        return;
                    }
                    analyzeViewModel.addProgress(name, first.countPowerProgress(), last.countPowerProgress(), true, "kg/s");
                });
                IntStream.range(0, 13).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusMonths(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusMonths(i);
                    final List<Training> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    exercises.forEach(name -> {
                        final var training = sub.stream().filter(t -> t.getExercises().getName().equalsIgnoreCase(name)).toList();
                        if (training.isEmpty()) {
                            return;
                        }
                        String month = finalSubTimeStart.getMonthValue() + "";
                        String year = finalSubTimeStart.getYear() + "";
                        if (finalSubTimeStart.getMonthValue() < 10) {
                            month = "0" + month;
                        }
                        analyzeViewModel.getCanvas().addLabel(year + "-" + month);

                        analyzeViewModel.getCanvas().addValue(name, training.stream().map(Training::countPowerProgress).reduce(0.0, Double::sum) / training.size());
                    });
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            default -> {
                return ResponseEntity.ok(new AnalyzeViewModel());
            }
        }
    }

    private String buildDate(final LocalDateTime start, final LocalDateTime end) {
        return this.buildDate(start) + " - " + this.buildDate(end);
    }
    private String buildDate(final LocalDateTime time) {
        String month = time.getMonthValue() + "";
        String day = time.getDayOfMonth() + "";
        if (time.getMonthValue() < 10) {
            month = "0" + month;
        }
        if (time.getDayOfMonth() < 10) {
            day = "0" + day;
        }
        return day+"-" + month;

    }
}
