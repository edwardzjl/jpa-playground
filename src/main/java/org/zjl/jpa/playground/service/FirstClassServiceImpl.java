package org.zjl.jpa.playground.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zjl.jpa.playground.dao.FirstClassRepo;
import org.zjl.jpa.playground.model.FirstClass;
import org.zjl.jpa.playground.model.FirstClass_;

import java.util.Optional;

/**
 * @author Junlin Zhou
 */
@Transactional
@Service
public class FirstClassServiceImpl implements FirstClassService {

    private final FirstClassRepo firstClassRepo;

    public FirstClassServiceImpl(FirstClassRepo firstClassRepo) {
        this.firstClassRepo = firstClassRepo;
    }

    /**
     * Calls {@link org.springframework.data.jpa.repository.JpaRepository#saveAndFlush(Object)}
     * If this was called by other functions, the returned entity was ensured of been update and in sync with the one in DB.
     */
    @Override
    public FirstClass updateWithFlush(FirstClass newInstance) {
        findByKeyField(newInstance.getKeyField())
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return firstClassRepo.saveAndFlush(newInstance);
    }

    /**
     * Calls {@link org.springframework.data.jpa.repository.JpaRepository#save(Object)}
     * If this was called by other functions, the returned entity may be different with the one in DB, because it's not flushed yet.
     */
    @Override
    public FirstClass updateWithoutFlush(FirstClass newInstance) {
        findByKeyField(newInstance.getKeyField())
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return firstClassRepo.save(newInstance);
    }

    @Override
    public Optional<FirstClass> findByKeyField(String keyField) {
        Specification<FirstClass> spec = (Specification<FirstClass>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(FirstClass_.keyField), keyField);
        return firstClassRepo.findOne(spec);
    }


}
