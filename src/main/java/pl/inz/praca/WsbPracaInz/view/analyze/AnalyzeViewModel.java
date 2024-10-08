package pl.inz.praca.WsbPracaInz.view.analyze;

import lombok.Getter;
import pl.inz.praca.WsbPracaInz.helper.MathHelper;
import pl.inz.praca.WsbPracaInz.model.BodySize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AnalyzeViewModel implements Serializable {
    private CanvasViewModel canvas;
    private List<ProgressViewModel> progress;

    public AnalyzeViewModel() {
        this.canvas = new CanvasViewModel();
        this.progress = new ArrayList<>();
    }

    public void addProgress(final String key, final Double first, final Double last, boolean positive, String unit) {
        if (first == null || last == null) {
            return;
        }
        if (first == 0 || last == 0) {
            return;
        }
        this.progress.add(new ProgressViewModel(key, first, last, positive, unit));
    }

    public void addBmi(final BodySize first, final BodySize last) {
        this.progress.add(new ProgressViewModel("BMI", first.getBmi(), last.getBmi(), false, ""));
    }
}
