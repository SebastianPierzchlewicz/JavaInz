package pl.inz.praca.WsbPracaInz.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.inz.praca.WsbPracaInz.auth.model.User;
import pl.inz.praca.WsbPracaInz.auth.service.UserService;
import pl.inz.praca.WsbPracaInz.model.BodySize;
import pl.inz.praca.WsbPracaInz.repo.BodySizeRepo;
import pl.inz.praca.WsbPracaInz.view.analyze.AnalyzeViewModel;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
public class BodySizeService {

    private final BodySizeRepo bodySizeRepo;
    private final UserService userService;

    public BodySizeService(BodySizeRepo bodySizeRepo, UserService userService) {
        this.bodySizeRepo = bodySizeRepo;
        this.userService = userService;
    }

    public void save(BodySize toSave) {
        this.bodySizeRepo.save(toSave);
    }

    public void addBodySize(final BodySize exercises, User user) {
        if (user == null) {
            return;
        }
        user.addBodySize(this.bodySizeRepo.save(exercises));
        this.userService.saveUser(user);
    }

    public List<BodySize> getAll() {
        return this.bodySizeRepo.findAll();
    }

    public List<BodySize> getAll(PageRequest pageRequest) {
        return this.bodySizeRepo.findAllBy(pageRequest);
    }

    public void delete(Long id) {
        this.bodySizeRepo.removeById(id);
    }

    public ResponseEntity<?> getAnalyze(final Authentication authentication, final String type) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getBodySizes().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        switch (type.toLowerCase()) {
            case "tydzien": {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusDays(7).withHour(23).withMinute(59).withSecond(59);
                final List<BodySize> value = user.getBodySizes().stream().filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(BodySize::getCreatedAt)).toList();
                final BodySize first = value.stream().sorted(Comparator.comparing(BodySize::getCreatedAt)).findFirst().orElse(null);
                final BodySize last = value.stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                if (first == null || last == null) {
                    return ResponseEntity.notFound().build();
                }
                analyzeViewModel.addBmi(first,last);
                analyzeViewModel.addProgress("Waga", first.getWeight(), last.getWeight(), false, "kg");
//                analyzeViewModel.addProgress("Wzrost", first.getHeight(), last.getHeight(), true, "cm");
                analyzeViewModel.addProgress("Szyja", first.getNeck(), last.getNeck(), false, "cm");
                analyzeViewModel.addProgress("Klatka", first.getChest(), last.getChest(), true, "cm");
                analyzeViewModel.addProgress("Biceps", first.getBiceps(), last.getBiceps(), true, "cm");
                analyzeViewModel.addProgress("Talia", first.getWaist(), last.getWaist(), false, "cm");
                analyzeViewModel.addProgress("Pas", first.getBelt(), last.getBelt(), false, "cm");
                analyzeViewModel.addProgress("Udo", first.getThigh(), last.getThigh(), true, "cm");
                analyzeViewModel.addProgress("Łydka", first.getCalf(), last.getCalf(), true, "cm");
                IntStream.range(0, 8).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusDays(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusDays(i);
                    final List<BodySize> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    if(sub.isEmpty()) {
                        return;
                    }
                    analyzeViewModel.getCanvas().addLabel(finalSubTimeStart.toLocalDate().toString());
                    analyzeViewModel.getCanvas().addBmi(sub);
                    analyzeViewModel.getCanvas().addValue("Waga", sub.stream().map(BodySize::getWeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
//                    analyzeViewModel.getCanvas().addValue("Wzrost", sub.stream().map(BodySize::getHeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Szyja", sub.stream().map(BodySize::getNeck).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Klatka", sub.stream().map(BodySize::getChest).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Biceps", sub.stream().map(BodySize::getBiceps).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Talia", sub.stream().map(BodySize::getWaist).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Pas", sub.stream().map(BodySize::getBelt).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Udo", sub.stream().map(BodySize::getThigh).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Łydka", sub.stream().map(BodySize::getCalf).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                });

                return ResponseEntity.ok(analyzeViewModel);
            }
            case "miesiac": {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusDays(31).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusDays(31).withHour(23).withMinute(59).withSecond(59);
                final List<BodySize> value = user.getBodySizes().stream().filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(BodySize::getCreatedAt)).toList();
                final BodySize first = value.stream().sorted(Comparator.comparing(BodySize::getCreatedAt)).findFirst().orElse(null);
                final BodySize last = value.stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                if (first == null || last == null) {
                    return ResponseEntity.notFound().build();
                }
                analyzeViewModel.addBmi(first,last);
                analyzeViewModel.addProgress("Waga", first.getWeight(), last.getWeight(), false, "kg");
//                analyzeViewModel.addProgress("Wzrost", first.getHeight(), last.getHeight(), true, "cm");
                analyzeViewModel.addProgress("Szyja", first.getNeck(), last.getNeck(), false, "cm");
                analyzeViewModel.addProgress("Klatka", first.getChest(), last.getChest(), true, "cm");
                analyzeViewModel.addProgress("Biceps", first.getBiceps(), last.getBiceps(), true, "cm");
                analyzeViewModel.addProgress("Talia", first.getWaist(), last.getWaist(), false, "cm");
                analyzeViewModel.addProgress("Pas", first.getBelt(), last.getBelt(), false, "cm");
                analyzeViewModel.addProgress("Udo", first.getThigh(), last.getThigh(), true, "cm");
                analyzeViewModel.addProgress("Łydka", first.getCalf(), last.getCalf(), true, "cm");

                IntStream.range(0, 32).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusDays(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusDays(i);
                    final List<BodySize> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    if(sub.isEmpty()) {
                        return;
                    }
                    String month = finalSubTimeStart.getMonthValue() +"";
                    String day = finalSubTimeStart.getDayOfMonth() +"";
                    if (finalSubTimeStart.getMonthValue() < 10) {
                        month = "0" + month;
                    }
                    if (finalSubTimeStart.getDayOfMonth() < 10) {
                       day = "0" + day;
                    }
                    analyzeViewModel.getCanvas().getLabel().add(month + "-" +day);
                    analyzeViewModel.getCanvas().addBmi(sub);
                    analyzeViewModel.getCanvas().addValue("Waga", sub.stream().map(BodySize::getWeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
//                    analyzeViewModel.getCanvas().addValue("Wzrost", sub.stream().map(BodySize::getHeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Szyja", sub.stream().map(BodySize::getNeck).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Klatka", sub.stream().map(BodySize::getChest).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Biceps", sub.stream().map(BodySize::getBiceps).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Talia", sub.stream().map(BodySize::getWaist).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Pas", sub.stream().map(BodySize::getBelt).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Udo", sub.stream().map(BodySize::getThigh).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Łydka", sub.stream().map(BodySize::getCalf).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            case "3miesiace": {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusWeeks(13).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusWeeks(13).withHour(23).withMinute(59).withSecond(59);
                final List<BodySize> value = user.getBodySizes().stream().filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(BodySize::getCreatedAt)).toList();
                final BodySize first = value.stream().sorted(Comparator.comparing(BodySize::getCreatedAt)).findFirst().orElse(null);
                final BodySize last = value.stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                if (first == null || last == null) {
                    return ResponseEntity.notFound().build();
                }
                analyzeViewModel.addBmi(first,last);
                analyzeViewModel.addProgress("Waga", first.getWeight(), last.getWeight(), false, "kg");
//                analyzeViewModel.addProgress("Wzrost", first.getHeight(), last.getHeight(), true, "cm");
                analyzeViewModel.addProgress("Szyja", first.getNeck(), last.getNeck(), false, "cm");
                analyzeViewModel.addProgress("Klatka", first.getChest(), last.getChest(), true, "cm");
                analyzeViewModel.addProgress("Biceps", first.getBiceps(), last.getBiceps(), true, "cm");
                analyzeViewModel.addProgress("Talia", first.getWaist(), last.getWaist(), false, "cm");
                analyzeViewModel.addProgress("Pas", first.getBelt(), last.getBelt(), false, "cm");
                analyzeViewModel.addProgress("Udo", first.getThigh(), last.getThigh(), true, "cm");
                analyzeViewModel.addProgress("Łydka", first.getCalf(), last.getCalf(), true, "cm");
                IntStream.range(0, 14).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusWeeks(i-1);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusWeeks(i);
                    final List<BodySize> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    if(sub.isEmpty()) {
                        return;
                    }
                    analyzeViewModel.getCanvas().addLabel(finalSubTimeStart.toLocalDate().toString());
                    analyzeViewModel.getCanvas().addBmi(sub);
                    analyzeViewModel.getCanvas().addValue("Waga", sub.stream().map(BodySize::getWeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
//                    analyzeViewModel.getCanvas().addValue("Wzrost", sub.stream().map(BodySize::getHeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Szyja", sub.stream().map(BodySize::getNeck).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Klatka", sub.stream().map(BodySize::getChest).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Biceps", sub.stream().map(BodySize::getBiceps).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Talia", sub.stream().map(BodySize::getWaist).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Pas", sub.stream().map(BodySize::getBelt).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Udo", sub.stream().map(BodySize::getThigh).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Łydka", sub.stream().map(BodySize::getCalf).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            case "6miesiecy": {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusMonths(6).with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
                final List<BodySize> value = user.getBodySizes().stream().filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(BodySize::getCreatedAt)).toList();
                final BodySize first = value.stream().sorted(Comparator.comparing(BodySize::getCreatedAt)).findFirst().orElse(null);
                final BodySize last = value.stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                if (first == null || last == null) {
                    return ResponseEntity.notFound().build();
                }
                analyzeViewModel.addBmi(first,last);
                analyzeViewModel.addProgress("Waga", first.getWeight(), last.getWeight(), false, "kg");
//                analyzeViewModel.addProgress("Wzrost", first.getHeight(), last.getHeight(), true, "cm");
                analyzeViewModel.addProgress("Szyja", first.getNeck(), last.getNeck(), false, "cm");
                analyzeViewModel.addProgress("Klatka", first.getChest(), last.getChest(), true, "cm");
                analyzeViewModel.addProgress("Biceps", first.getBiceps(), last.getBiceps(), true, "cm");
                analyzeViewModel.addProgress("Talia", first.getWaist(), last.getWaist(), false, "cm");
                analyzeViewModel.addProgress("Pas", first.getBelt(), last.getBelt(), false, "cm");
                analyzeViewModel.addProgress("Udo", first.getThigh(), last.getThigh(), true, "cm");
                analyzeViewModel.addProgress("Łydka", first.getCalf(), last.getCalf(), true, "cm");
                IntStream.range(0, 7).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusMonths(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusMonths(i);
                    String month = finalSubTimeStart.getMonthValue() +"";
                    String year = finalSubTimeStart.getYear() +"";
                    if (finalSubTimeStart.getMonthValue() < 10) {
                        month = "0" + month;
                    }
                    final List<BodySize> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    if(sub.isEmpty()) {
                        return;
                    }
                    analyzeViewModel.getCanvas().addLabel(year+"-"+month);
                    analyzeViewModel.getCanvas().addBmi(sub);
                    analyzeViewModel.getCanvas().addValue("Waga", sub.stream().map(BodySize::getWeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
//                    analyzeViewModel.getCanvas().addValue("Wzrost", sub.stream().map(BodySize::getHeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Szyja", sub.stream().map(BodySize::getNeck).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Klatka", sub.stream().map(BodySize::getChest).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Biceps", sub.stream().map(BodySize::getBiceps).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Talia", sub.stream().map(BodySize::getWaist).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Pas", sub.stream().map(BodySize::getBelt).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Udo", sub.stream().map(BodySize::getThigh).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Łydka", sub.stream().map(BodySize::getCalf).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            case "rok": {
                final AnalyzeViewModel analyzeViewModel = new AnalyzeViewModel();
                final LocalDateTime subTimeStart = LocalDateTime.now().minusMonths(12).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
                final LocalDateTime subTimeEnd = LocalDateTime.now().minusMonths(12).with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
                final List<BodySize> value = user.getBodySizes().stream().filter(v -> v.getCreatedAt().compareTo(subTimeStart) >= 0).sorted(Comparator.comparing(BodySize::getCreatedAt)).toList();
                final BodySize first = value.stream().sorted(Comparator.comparing(BodySize::getCreatedAt)).findFirst().orElse(null);
                final BodySize last = value.stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).findFirst().orElse(null);
                if (first == null || last == null) {
                    return ResponseEntity.notFound().build();
                }
                analyzeViewModel.addBmi(first,last);
                analyzeViewModel.addProgress("Waga", first.getWeight(), last.getWeight(), false, "kg");
//                analyzeViewModel.addProgress("Wzrost", first.getHeight(), last.getHeight(), true, "cm");
                analyzeViewModel.addProgress("Szyja", first.getNeck(), last.getNeck(), false, "cm");
                analyzeViewModel.addProgress("Klatka", first.getChest(), last.getChest(), true, "cm");
                analyzeViewModel.addProgress("Biceps", first.getBiceps(), last.getBiceps(), true, "cm");
                analyzeViewModel.addProgress("Talia", first.getWaist(), last.getWaist(), false, "cm");
                analyzeViewModel.addProgress("Pas", first.getBelt(), last.getBelt(), false, "cm");
                analyzeViewModel.addProgress("Udo", first.getThigh(), last.getThigh(), true, "cm");
                analyzeViewModel.addProgress("Łydka", first.getCalf(), last.getCalf(), true, "cm");

                IntStream.range(0, 13).forEach(i -> {
                    final LocalDateTime finalSubTimeStart = subTimeStart.plusMonths(i);
                    final LocalDateTime finalSubTimeEnd = subTimeEnd.plusMonths(i);
                    String month = finalSubTimeStart.getMonthValue() +"";
                    String year = finalSubTimeStart.getYear() +"";
                    if (finalSubTimeStart.getMonthValue() < 10) {
                        month = "0" + month;
                    }
                    final List<BodySize> sub = value.stream().filter(v -> v.getCreatedAt().compareTo(finalSubTimeStart) >= 0).filter(v -> v.getCreatedAt().compareTo(finalSubTimeEnd) <= 0).toList();
                    if(sub.isEmpty()) {
                        return;
                    }
                    analyzeViewModel.getCanvas().addLabel(year+"-"+month);
                    analyzeViewModel.addBmi(first,last);
                    analyzeViewModel.getCanvas().addBmi(sub);
                    analyzeViewModel.getCanvas().addValue("Waga", sub.stream().map(BodySize::getWeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
//                    analyzeViewModel.getCanvas().addValue("Wzrost", sub.stream().map(BodySize::getHeight).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Szyja", sub.stream().map(BodySize::getNeck).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Klatka", sub.stream().map(BodySize::getChest).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Biceps", sub.stream().map(BodySize::getBiceps).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Talia", sub.stream().map(BodySize::getWaist).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Pas", sub.stream().map(BodySize::getBelt).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Udo", sub.stream().map(BodySize::getThigh).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                    analyzeViewModel.getCanvas().addValue("Łydka", sub.stream().map(BodySize::getCalf).filter(Objects::nonNull).reduce(0.0, Double::sum) / sub.size());
                });
                return ResponseEntity.ok(analyzeViewModel);
            }
            default: {
                return ResponseEntity.ok(new AnalyzeViewModel());
            }
        }
    }
}
