package org.zjl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zjl.model.ClassRelation;

@Repository
public interface ClassRelationRepo extends JpaRepository<ClassRelation, ClassRelation.ClassRelationId> {
}
