package com.sixb.note.repository;

import com.sixb.note.entity.PageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageDataRepository extends MongoRepository<PageData, String>, PageDataRepositoryCustom {

}
