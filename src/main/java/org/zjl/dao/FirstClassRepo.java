package org.zjl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zjl.model.FirstClass;

import java.util.Optional;

/**
 * @author Junlin Zhou
 */
@Repository
public interface FirstClassRepo extends JpaRepository<FirstClass, Long> {

    Optional<FirstClass> findByKeyField(String keyField);

}
