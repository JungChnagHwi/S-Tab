package com.sixb.stab.auth.repository;

import com.sixb.stab.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.*;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Optional<RefreshToken> findByUserId(long userId);

}
