package org.zjl.jpa.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Junlin Zhou
 */
@Entity
@Table(name = "second_class")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecondClass {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "key_field")
    private String keyField;

    @JsonIgnore
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = ClassRelation_.SECOND_CLASS, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassRelation> firstClassInstances = new HashSet<>();

    public void addFirstClass(FirstClass instance) {
        ClassRelation relation = new ClassRelation(this, instance);
        firstClassInstances.add(relation);
        instance.getSecondClassInstances().add(relation);
    }

    public void clearRelations() {
        firstClassInstances.forEach(relation ->
                relation.getSecondClass().getFirstClassInstances().remove(relation));
        firstClassInstances.clear();
    }

    @PreRemove
    private void onRemove() {
        clearRelations();
    }

}
