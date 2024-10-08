package pl.inz.praca.WsbPracaInz.trening.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExercisesControllerTest {

    @Test
    public void test() {
        final double max =2;

        int totalPages = (int) (Math.ceil(3 / max) );
        int totalPages1 = (int) (Math.ceil(4 / max) );
        assertEquals(totalPages,2);
        assertEquals(totalPages1,2);
    }

}