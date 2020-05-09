package org.zjl.jpa.playground.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.zjl.jpa.playground.dao.SecondClassRepo;
import org.zjl.jpa.playground.model.SecondClass;
import org.zjl.jpa.playground.model.SecondClass_;

import java.util.Optional;

@Service
public class SecondClassServiceImpl implements SecondClassService {

    private final SecondClassRepo secondClassRepo;

    public SecondClassServiceImpl(SecondClassRepo secondClassRepo) {
        this.secondClassRepo = secondClassRepo;
    }

    @Override
    public SecondClass updateWithFlush(SecondClass newInstance) {
        findByKeyField(newInstance.getKeyField())
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return secondClassRepo.saveAndFlush(newInstance);
    }

    @Override
    public SecondClass updateWithoutFlush(SecondClass newInstance) {
        findByKeyField(newInstance.getKeyField())
                .ifPresent(oldInstance -> {
                    oldInstance.clearRelations();
                    newInstance.setId(oldInstance.getId());
                });
        return secondClassRepo.save(newInstance);
    }

    @Override
    public Optional<SecondClass> findByKeyField(String keyField) {
        Specification<SecondClass> spec = (Specification<SecondClass>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(SecondClass_.keyField), keyField);
        return secondClassRepo.findOne(spec);
    }
}
