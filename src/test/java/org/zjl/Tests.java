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
import org.zjl.jpa.playground.dao.ClassRelationRepo;
import org.zjl.jpa.playground.dao.FirstClassRepo;
import org.zjl.jpa.playground.dao.SecondClassRepo;
import org.zjl.jpa.playground.model.FirstClass;
import org.zjl.jpa.playground.model.SecondClass;
import org.zjl.jpa.playground.service.FirstClassService;
import org.zjl.jpa.playground.service.FirstClassServiceImpl;
import org.zjl.jpa.playground.service.SecondClassService;

import javax.persistence.EntityNotFoundException;


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
    private SecondClassService secondClassService;
    @Autowired
    private ClassRelationRepo classRelationRepo;

    // =============== environment setup ===============

    /**
     * Clear all instances. Run this independently before {@link #setup()} to ensure a flush.
     */
    // TODO: 2020/3/22 zjl one method to do both clear and setup
    @Test
    public void clear() {
        firstClassRepo.deleteAll();
        secondClassRepo.deleteAll();
    }

    /**
     * Setup one instance for each class and a relation between them.
     */
    @Test
    public void setup() {
        FirstClass firstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("foo1").build();
        firstClassInstance = firstClassRepo.save(firstClassInstance);
        SecondClass secondClassInstance = SecondClass.builder().keyField("secondClassKey1").build();
        secondClassInstance = secondClassRepo.save(secondClassInstance);
        firstClassInstance.addSecondClass(secondClassInstance);
    }

    // =============== test cases ===============

    /**
     *
     */
    @Test
    public void safeUpdate() {
        FirstClass firstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("foo1").build();
        firstClassService.updateWithoutFlush(firstClassInstance);
    }

    /**
     * In this example, orphan removal works and when removing relation in first class, relation in second class also get removed.
     */
    @Test
    public void checkRemove() {
        firstClassRepo.findByKeyField("firstClassKey1")
                .ifPresent(FirstClass::clearRelations);
    }

    /**
     * Without flush nor old relation.
     * After this there's only one relation in DB, which is between new instances.
     * It means that our {@link FirstClass#clearRelations()} in {@link FirstClassServiceImpl#updateWithoutFlush(FirstClass)} works.
     */
    @Test
    public void withoutFlushNorOldRelation() {
        FirstClass newFirstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("newFoo").build();
        newFirstClassInstance = firstClassService.updateWithoutFlush(newFirstClassInstance);

        SecondClass newSecondClassInstance = SecondClass.builder().keyField("newKey2").build();
        newSecondClassInstance = secondClassRepo.save(newSecondClassInstance);

        newFirstClassInstance.addSecondClass(newSecondClassInstance);
    }

    /**
     * Without flush, but this time we kept the old relation.
     * An Exception will be thrown saying 'deleted object would be re-saved by cascade'
     * It's because although we saved the updated first class instance, it's not flushed yet, and the second class instance
     * we aquired will still be bolding the old (should been deleted) relation.
     * <p>
     * For a correct use case see {@link #withFlushWithOldRelation()}
     */
    @Test
    public void withoutFlushWithOldRelation() {
        FirstClass newFirstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("newFoo").build();
        newFirstClassInstance = firstClassService.updateWithoutFlush(newFirstClassInstance);
        // 上一步 clear 了 relation, 但是这里取出来的 second class 里还是带有 relation, 因为还没 flush
        SecondClass oldSecondClassInstance = secondClassRepo.findByKeyField("secondClassKey1")
                .orElseThrow(EntityNotFoundException::new);

        SecondClass newSecondClassInstance = SecondClass.builder().keyField("newKey2").build();
        newSecondClassInstance = secondClassRepo.save(newSecondClassInstance);

        newFirstClassInstance.addSecondClass(oldSecondClassInstance);
        newFirstClassInstance.addSecondClass(newSecondClassInstance);
    }

    /**
     * The most important test case.
     * Core is {@link org.springframework.data.jpa.repository.JpaRepository#saveAndFlush(Object)}
     * For more details check <a href="https://www.baeldung.com/spring-data-jpa-save-saveandflush">Save and SaveAndFlush</a>
     */
    @Test
    public void withFlushWithOldRelation() {
        FirstClass newFirstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("newFoo").build();
        newFirstClassInstance = firstClassService.updateWithFlush(newFirstClassInstance);

        SecondClass oldSecondClassInstance = secondClassRepo.findByKeyField("secondClassKey1")
                .orElseThrow(EntityNotFoundException::new);
        SecondClass newSecondClassInstance = SecondClass.builder().keyField("newKey2").build();
        newSecondClassInstance = secondClassRepo.save(newSecondClassInstance);

        newFirstClassInstance.addSecondClass(oldSecondClassInstance);
        newFirstClassInstance.addSecondClass(newSecondClassInstance);
    }

    /**
     * this time, we create a new Entity, which was already persisted in db with its relation.
     * It works because our {@link org.zjl.jpa.playground.model.ClassRelation} implements its equals and hashcode on its Id.
     */
    @Test
    public void withNewlyCreatedOldRelation() {
        FirstClass newFirstClassInstance = FirstClass.builder().keyField("firstClassKey1").fooField("newFoo").build();
        newFirstClassInstance = firstClassService.updateWithFlush(newFirstClassInstance);
        // this is an "old entity" which already exists in db. Its relation also exists.
        SecondClass existedSecondClassInstance = SecondClass.builder().keyField("secondClassKey1").build();
        existedSecondClassInstance = secondClassService.updateWithFlush(existedSecondClassInstance);
        // this is a new entity
        SecondClass newSecondClassInstance = SecondClass.builder().keyField("newKey2").build();
        newSecondClassInstance = secondClassService.updateWithFlush(newSecondClassInstance);
        // add both to first instance.
        newFirstClassInstance.addSecondClass(existedSecondClassInstance);
        newFirstClassInstance.addSecondClass(newSecondClassInstance);
        // we don't even need to save, as newFirstClassInstance was a managed entity
    }

    // =============== printers ===============

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
