package org.zjl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zjl.model.SecondClass;

import java.util.Optional;

/**
 * @author Junlin Zhou
 */
@Repository
public interface SecondClassRepo extends JpaRepository<SecondClass, Long> {

    Optional<SecondClass> findByKeyField(String keyField);

}
