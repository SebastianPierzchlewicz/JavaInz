package pl.inz.praca.WsbPracaInz.repo;

import pl.inz.praca.WsbPracaInz.model.Series;

public interface SeriesRepo
{

    Series save(Series series);

    void deleteById(final Long id);
}
