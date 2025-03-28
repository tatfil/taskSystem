package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;


    public Optional<User> getById(Long userId) {

        logger.info("Fetching user {}", userId);

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            logger.info("User found: {}", userId);
            return user;
        } else {
            logger.warn("No user found with {}", userId);
        }
        return user;
    }
}
