package com.sixb.note.repository;

import com.sixb.note.entity.PageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageDataRepository extends MongoRepository<PageData, String> {

    Optional<PageData> findById(String id);
}
