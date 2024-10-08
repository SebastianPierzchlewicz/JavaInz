package pl.inz.praca.WsbPracaInz.view.analyze;

import lombok.Getter;
import pl.inz.praca.WsbPracaInz.helper.MathHelper;
import pl.inz.praca.WsbPracaInz.model.BodySize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CanvasViewModel implements Serializable {

    private List<String> label;
    private List<CancansDataViewModel> values;

    public CanvasViewModel() {
        this.label = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public void addLabel(final String string) {
        this.label.add(string);
    }

    public void addValue(String name, Double value) {
        if (value == null || value.isNaN() || value == 0) {
            return;
        }
        final CancansDataViewModel data = this.values.stream().filter(cancansDataViewModel -> cancansDataViewModel.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (data == null) {
            final List<Double> list = new ArrayList<>();
            list.add(value.isNaN() ? 0 : MathHelper.round(value,2));
            this.values.add(new CancansDataViewModel(name, list));
            return;
        }
        data.addValue(value);
    }

    public void addBmi(final List<BodySize> first) {
        final double avgWeight = first.stream().map(BodySize::getWeight).reduce(0.0, Double::sum) / first.size();
        final double avgHeight= first.stream().map(BodySize::getHeight).reduce(0.0, Double::sum) / first.size();
        if (avgWeight == 0 && avgHeight == 0) {
            return;
        }
        this.addValue("BMI", MathHelper.countBmi(avgWeight, avgHeight));
    }

}
