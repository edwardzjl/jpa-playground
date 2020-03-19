package org.zjl.model;

import lombok.*;

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

    @Column(name = "key_field")
    private String keyField;

    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "secondClass", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassRelation> firstClassInstances = new HashSet<>();

}
