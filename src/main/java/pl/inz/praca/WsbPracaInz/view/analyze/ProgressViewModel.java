package pl.inz.praca.WsbPracaInz.view.analyze;

import lombok.Getter;
import pl.inz.praca.WsbPracaInz.helper.MathHelper;

@Getter
public class ProgressViewModel {


    private String name;
    private Double first;
    private Double last;
    private Double progress;
    private boolean positive;
    private String unit;


    public ProgressViewModel(final String name, Double first, Double last, boolean positive, String unit) {
        this.name = name;
        this.first = (first.isNaN() ? 0 :first);
        this.last = (last.isNaN() ? 0 : last);
        if (this.first.compareTo(this.last) == 0) {
            this.progress= 0.0;
        }
        else if (this.first.compareTo(this.last) > 0) {
            this.progress = -1 * (Math.abs(MathHelper.round(100 - ((this.last *100) / this.first),2)));
        }
        else {
            this.progress =  Math.abs(MathHelper.round(100 - ((this.last *100) / this.first),2));
        }
        this.positive = (positive ? this.progress > 0 : this.progress < 0);
        this.unit = unit;
    }
}
