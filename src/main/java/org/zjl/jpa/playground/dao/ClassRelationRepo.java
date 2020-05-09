package org.zjl.jpa.playground.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zjl.jpa.playground.model.ClassRelation;

/**
 * @author Junlin Zhou
 */
@Repository
public interface ClassRelationRepo extends JpaRepository<ClassRelation, ClassRelation.ClassRelationId> {
}
