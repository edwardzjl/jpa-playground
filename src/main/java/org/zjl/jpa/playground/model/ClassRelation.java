package org.zjl.jpa.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @MapsId("firstClassId")
    private FirstClass firstClass;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @MapsId("secondClassId")
    private SecondClass secondClass;

    public ClassRelation(FirstClass firstClass, SecondClass secondClass) {
        this.firstClass = firstClass;
        this.secondClass = secondClass;
        this.id = new ClassRelationId(firstClass.getId(), secondClass.getId());
    }

    public ClassRelation(SecondClass secondClass, FirstClass firstClass) {
        this(firstClass, secondClass);
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
