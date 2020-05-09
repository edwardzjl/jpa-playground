package org.zjl.jpa.playground.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.zjl.jpa.playground.model.FirstClass;

import java.util.Optional;

/**
 * @author Junlin Zhou
 */
@Repository
public interface FirstClassRepo extends JpaRepository<FirstClass, Long>, JpaSpecificationExecutor<FirstClass> {

    Optional<FirstClass> findByKeyField(String keyField);

}
