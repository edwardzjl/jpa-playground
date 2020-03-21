package org.zjl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zjl.dao.FirstClassRepo;
import org.zjl.model.FirstClass;
import org.zjl.model.FirstClass_;

/**
 * @author Junlin Zhou
 */
@Transactional
@Service
public class FirstClassService {

    private final FirstClassRepo firstClassRepo;

    public FirstClassService(FirstClassRepo firstClassRepo) {
        this.firstClassRepo = firstClassRepo;
    }

    /**
     * Correct example of updating, calls {@link org.springframework.data.jpa.repository.JpaRepository#saveAndFlush(Object)}
     * If this was called by other functions, the returned entity was ensured of been update and in sync with the one in DB.
     */
    public FirstClass correctUpdate(FirstClass newInstance) {
        Specification<FirstClass> spec = (Specification<FirstClass>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(FirstClass_.keyField), newInstance.getKeyField());

        firstClassRepo.findOne(spec)
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return firstClassRepo.saveAndFlush(newInstance);
    }

    /**
     * Wrong example of updating, calls {@link org.springframework.data.jpa.repository.JpaRepository#save(Object)}
     * If this was called by other functions, the returned entity may be different with the one in DB, because it's not flushed yet.
     */
    public FirstClass wrongUpdate(FirstClass newInstance) {
        Specification<FirstClass> spec = (Specification<FirstClass>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(FirstClass_.keyField), newInstance.getKeyField());

        firstClassRepo.findOne(spec)
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return firstClassRepo.save(newInstance);
    }

}
