package org.zjl.jpa.playground.service;

import org.zjl.jpa.playground.model.SecondClass;

import java.util.Optional;

public interface SecondClassService {

    SecondClass updateWithFlush(SecondClass newInstance);

    SecondClass updateWithoutFlush(SecondClass newInstance);

    Optional<SecondClass> findByKeyField(String keyField);

}
