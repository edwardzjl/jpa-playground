package org.zjl.jpa.playground.service;

import org.zjl.jpa.playground.model.FirstClass;

import java.util.Optional;

public interface FirstClassService {

    FirstClass updateWithFlush(FirstClass newInstance);

    FirstClass updateWithoutFlush(FirstClass newInstance);

    Optional<FirstClass> findByKeyField(String keyField);

}
