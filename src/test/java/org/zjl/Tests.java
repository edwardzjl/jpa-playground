package org.zjl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zjl.dao.ClassRelationRepo;
import org.zjl.dao.FirstClassRepo;
import org.zjl.dao.SecondClassRepo;
import org.zjl.model.FirstClass;
import org.zjl.model.SecondClass;

import java.util.Optional;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class Tests {

    @Autowired
    private FirstClassRepo firstClassRepo;

    @Autowired
    private SecondClassRepo secondClassRepo;

    @Autowired
    private FirstClassService firstClassService;

    @Autowired
    private ClassRelationRepo classRelationRepo;

    @Before
    public void setup() {
        FirstClass firstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("foo1").build();
        firstClassRepo.save(firstClassInstance);
        SecondClass secondClassInstance = SecondClass.builder().keyField("secondClassKey1").build();
        secondClassRepo.save(secondClassInstance);
        firstClassInstance.addSecondClass(secondClassInstance);
    }


    @Test
    public void checkSetup() {
        firstClassRepo.findByKeyField("firstClassKey1").ifPresent(
                System.out::println
        );
        secondClassRepo.findByKeyField("secondClassKey1").ifPresent(
                System.out::println
        );
    }

    /**
     * In this example, orphan removal works and when removing relation in first class, relation in second class also been removed.
     */
    @Test
    public void checkRemove() {
        Optional<FirstClass> optionalOldInstance = firstClassRepo.findByKeyField("firstClassKey1");
        if (optionalOldInstance.isPresent()) {
            FirstClass oldFirstClassInstance = optionalOldInstance.get();
            oldFirstClassInstance.clearRelations();
            firstClassRepo.save(oldFirstClassInstance);
        }
        secondClassRepo.findByKeyField("secondClassKey1").ifPresent(secondClass -> {
            secondClass.getFirstClassInstances().forEach(relation -> System.out.println(relation.getFirstClass()));
        });
    }

    @Test
    public void checkRemoveWithoutSave() {
        Optional<FirstClass> optionalOldInstance = firstClassRepo.findByKeyField("firstClassKey1");
        if (optionalOldInstance.isPresent()) {
            FirstClass oldFirstClassInstance = optionalOldInstance.get();
            oldFirstClassInstance.clearRelations();
        }
    }

    /**
     * 新的 entity 中关联关系的实体先 persist 了, 之后只要将新 entity id 赋值为旧 entity 的 id, 直接存新 entity 即可
     */
    @Transactional
    @Test
    public void checkReplace() {
        FirstClass newFirstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("newFoo").build();
        newFirstClassInstance = firstClassService.update(newFirstClassInstance);

        SecondClass newSecondClassInstance = SecondClass.builder().keyField("newKey2").build();
        newSecondClassInstance = secondClassRepo.save(newSecondClassInstance);

        newFirstClassInstance.addSecondClass(newSecondClassInstance);
    }

    /**
     * In this example I did not add {@code @Transactional} annotation, and the the relation get removed.
     * But the new relation is not persisted instead.
     */
    @Test
    public void checkReplace2() {
        FirstClass newFirstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("newFoo").build();
        newFirstClassInstance = firstClassService.update(newFirstClassInstance);

        SecondClass newSecondClassInstance = SecondClass.builder().keyField("newKey2").build();
        newSecondClassInstance = secondClassRepo.save(newSecondClassInstance);

        newFirstClassInstance.addSecondClass(newSecondClassInstance);
    }

    @After
    public void afterTransactionalEffect() {
        pringAllRelations();
        printFirstClassInstances();
        printSecondClassInstances();
    }

    private void pringAllRelations() {
        System.out.println("------------------- all relations ---------------------------------------");
        classRelationRepo.findAll().forEach(relation -> {
            System.out.println(relation.getFirstClass() + " to " + relation.getSecondClass());
        });
    }

    private void printFirstClassInstances() {
        System.out.println("------------------- first class instances ---------------------------------------");
        firstClassRepo.findAll().forEach(firstClassInstance -> {
            System.out.println(firstClassInstance);
            firstClassInstance.getSecondClassInstances().forEach(relation -> {
                System.out.println(relation.getSecondClass());
            });
        });
    }

    private void printSecondClassInstances() {
        System.out.println("------------------- second class instances ---------------------------------------");
        secondClassRepo.findAll().forEach(secondClassInstance -> {
            System.out.println(secondClassInstance);
            secondClassInstance.getFirstClassInstances().forEach(relation -> {
                System.out.println(relation.getFirstClass());
            });
        });
    }
}
