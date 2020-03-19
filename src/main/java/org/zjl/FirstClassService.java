package org.zjl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zjl.dao.FirstClassRepo;
import org.zjl.model.FirstClass;

/**
 * @author Junlin Zhou
 */
@Service
public class FirstClassService {

    @Autowired
    private FirstClassRepo firstClassRepo;

    @Transactional
    public FirstClass update(FirstClass newInstance) {
        firstClassRepo.findByKeyField(newInstance.getKeyField())
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return firstClassRepo.save(newInstance);
    }

}
