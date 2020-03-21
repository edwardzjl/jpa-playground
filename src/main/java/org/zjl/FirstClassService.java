package org.zjl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zjl.dao.FirstClassRepo;
import org.zjl.model.FirstClass;

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


    public FirstClass update(FirstClass newInstance) {
        Specification<FirstClass> spec = (Specification<FirstClass>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("keyField"), newInstance.getKeyField());

        firstClassRepo.findOne(spec)
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return firstClassRepo.save(newInstance);
    }

}
