package org.zjl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.zjl.dao.ClassRelationRepo;
import org.zjl.dao.FirstClassRepo;
import org.zjl.dao.SecondClassRepo;
import org.zjl.model.FirstClass;
import org.zjl.model.SecondClass;

import java.util.Optional;


/**
 * Playground tests to demostrate how to update jpa entity with relation.
 * <p>
 * Do not use {@link Before} to setup environment,
 * as the setup method will be unioned to the test method, and there will be no flush stage between.
 *
 * @author Junlin Zhou
 */
@ActiveProfiles({"test"})
@Rollback(false)
@Transactional
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

    // ====================================

    @Test
    public void setup() {
        firstClassRepo.deleteAll();
        secondClassRepo.deleteAll();
        classRelationRepo.deleteAll();
        FirstClass firstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("foo1").build();
        firstClassInstance = firstClassRepo.save(firstClassInstance);
        SecondClass secondClassInstance = SecondClass.builder().keyField("secondClassKey1").build();
        secondClassInstance = secondClassRepo.save(secondClassInstance);
        firstClassInstance.addSecondClass(secondClassInstance);
    }

    @Test
    public void clear() {
        firstClassRepo.deleteAll();
        secondClassRepo.deleteAll();
        classRelationRepo.deleteAll();
    }

    // ====================================

    /**
     * In this example, orphan removal works and when removing relation in first class, relation in second class also get removed.
     */
    @Test
    public void checkRemove() {
        Optional<FirstClass> optionalOldInstance = firstClassRepo.findByKeyField("firstClassKey1");
        if (optionalOldInstance.isPresent()) {
            FirstClass oldFirstClassInstance = optionalOldInstance.get();
            oldFirstClassInstance.clearRelations();
        }
    }

    /**
     * 新的 entity 中关联关系的实体先 persist 了, 之后只要将新 entity id 赋值为旧 entity 的 id, 直接存新 entity 即可
     */
    @Test
    public void checkReplace() {
        FirstClass newFirstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("newFoo").build();
        newFirstClassInstance = firstClassService.update(newFirstClassInstance);

        SecondClass newSecondClassInstance = SecondClass.builder().keyField("newKey2").build();
        newSecondClassInstance = secondClassRepo.save(newSecondClassInstance);

        newFirstClassInstance.addSecondClass(newSecondClassInstance);
    }

    // ====================================

    private void pringAllRelations() {
        System.out.println();
        System.out.println("------------------- all relations ---------------------------------------");
        classRelationRepo.findAll().forEach(relation -> {
            System.out.println(relation.getFirstClass() + " to " + relation.getSecondClass());
        });
        System.out.println("--- end printing all relations ---");
    }

    private void printFirstClassInstances() {
        System.out.println();
        System.out.println("------------------- first class instances ---------------------------------------");
        firstClassRepo.findAll().forEach(firstClassInstance -> {
            System.out.println(firstClassInstance);
            firstClassInstance.getSecondClassInstances().forEach(relation -> {
                System.out.println("SecondClass in FirstClass: " + relation.getSecondClass());
            });
        });
        System.out.println("--- end printing first class instances ---");
    }

    private void printSecondClassInstances() {
        System.out.println();
        System.out.println("------------------- second class instances ---------------------------------------");
        secondClassRepo.findAll().forEach(secondClassInstance -> {
            System.out.println(secondClassInstance);
            secondClassInstance.getFirstClassInstances().forEach(relation -> {
                System.out.println("FirstClass in Second: " + relation.getFirstClass());
            });
        });
        System.out.println("--- end printing second class instances ---");
    }
}
