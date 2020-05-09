package org.zjl.jpa.playground.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.zjl.jpa.playground.model.SecondClass;

import java.util.Optional;

/**
 * @author Junlin Zhou
 */
@Repository
public interface SecondClassRepo extends JpaRepository<SecondClass, Long>, JpaSpecificationExecutor<SecondClass> {

    Optional<SecondClass> findByKeyField(String keyField);

}
