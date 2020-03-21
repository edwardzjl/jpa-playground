package org.zjl.model;

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
@Table(name = "first_class")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirstClass {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "key_field", unique = true)
    private String keyField;

    @Column(name = "foo_field")
    private String fooField;

    @JsonIgnore
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = ClassRelation_.FIRST_CLASS, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassRelation> secondClassInstances = new HashSet<>();

    public void addSecondClass(SecondClass instance) {
        ClassRelation relation = new ClassRelation(this, instance);
        secondClassInstances.add(relation);
        instance.getFirstClassInstances().add(relation);
    }

    public void clearRelations() {
        secondClassInstances.forEach(relation ->
                relation.getSecondClass().getFirstClassInstances().remove(relation));
        secondClassInstances.clear();
    }

    @PreRemove
    private void onRemove() {
        clearRelations();
    }

}
