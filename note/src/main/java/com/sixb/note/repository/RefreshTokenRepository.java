package com.sixb.note.repository;

import com.sixb.note.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    RefreshToken findByUserId(long userId);
}
