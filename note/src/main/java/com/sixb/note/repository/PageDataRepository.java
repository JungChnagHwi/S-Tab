package com.sixb.note.repository;

import com.sixb.note.dto.pageData.PageDataDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageDataRepository extends MongoRepository<PageDataDto, String> {

    Optional<PageDataDto> findById(String id);
}
