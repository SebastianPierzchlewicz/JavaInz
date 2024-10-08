package pl.inz.praca.WsbPracaInz.view.analyze;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.inz.praca.WsbPracaInz.helper.MathHelper;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class CancansDataViewModel implements Serializable
{
    private String name;
    private List<Double> data;


    public void addValue(Double value) {
        if (value == null || value.isNaN()) {
            value = 0.0;
        }
        this.data.add(MathHelper.round(value,2));
    }
}
