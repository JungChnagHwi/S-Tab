package com.sixb.note.api.service;

import com.sixb.note.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sixb.note.entity.User;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public List<User> findUsersBySpaceId(UUID spaceId) {
        return userRepository.findUsersBySpaceId(spaceId);
    }
}
