package org.zjl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Junlin Zhou
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "class_relation")
public class ClassRelation {

    @EmbeddedId
    private ClassRelationId id;

    @ManyToOne
    @MapsId("firstClassId")
    private FirstClass firstClass;

    @ManyToOne
    @MapsId("secondClassId")
    private SecondClass secondClass;

    public ClassRelation(FirstClass firstClass, SecondClass secondClass) {
        this.firstClass = firstClass;
        this.secondClass = secondClass;
        this.id = new ClassRelationId(firstClass.getId(), secondClass.getId());
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class ClassRelationId implements Serializable {

        @Column(name = "first_class_id")
        private Long firstClassId;

        @Column(name = "second_class_id")
        private Long secondClassId;
    }
}
