package pl.inz.praca.WsbPracaInz.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
@Getter
@AllArgsConstructor
public class DashboardViewModel implements Serializable {

        private final String name;
        private final int amount;
        private final String last;
        private final String icon;
}
