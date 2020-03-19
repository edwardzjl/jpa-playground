package org.zjl.model;

import lombok.*;

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

    @Column(name = "key_field")
    private String keyField;

    @Column(name = "foo_field")
    private String fooField;

    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "firstClass", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassRelation> secondClassInstances = new HashSet<>();

    public void addSecondClass(SecondClass instance) {
        ClassRelation relation = new ClassRelation(this, instance);
        secondClassInstances.add(relation);
        instance.getFirstClassInstances().add(relation);
    }

    public void clearRelations() {
        secondClassInstances.forEach(relation -> relation.getSecondClass().getFirstClassInstances().remove(relation));
        secondClassInstances.clear();
    }

}
