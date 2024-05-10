package com.sixb.note.repository;

import com.sixb.note.entity.PageData;
import org.springframework.data.mongodb.core.MongoOperations;

public class PageDataRepositoryCusomImpl implements PageDataRepositoryCustom {

    private final MongoOperations mongoOperations;

    public PageDataRepositoryCusomImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public PageData findDataById(String id) {
        return mongoOperations.findById(id, PageData.class);
    }

}
