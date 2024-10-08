package pl.inz.praca.WsbPracaInz.repo;

import org.springframework.data.domain.PageRequest;
import pl.inz.praca.WsbPracaInz.model.BodySize;

import java.util.List;
import java.util.Optional;

public interface BodySizeRepo {

    BodySize save(BodySize toSave);

    Optional<BodySize> findById(long id);

    List<BodySize> findAll();

    List<BodySize> findAllBy(PageRequest pageRequest);

    void removeById(long id);
}
