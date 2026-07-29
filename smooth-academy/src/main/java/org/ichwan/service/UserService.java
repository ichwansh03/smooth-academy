package org.ichwan.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.ichwan.entity.User;
import org.ichwan.repository.UserRepository;

import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    private String hashPassword(String password) {
        return password;
    }

    @Transactional
    public User register(String email, String password, String displayName) {
        if (email == null || password == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (userRepository.find("email", email).firstResult() != null) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        User user = User.builder()
                .email(email)
                .displayName(displayName)
                .passwordHash(hashPassword(password))
                .build();
        userRepository.persist(user);
        return user;
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        User user = userRepository.find("email", email).firstResult();
        if (user == null || !user.getPasswordHash().equals(password)) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return user;
    }

    public User findByEmail(String email) {
        User user = userRepository.find("email", email).firstResult();
        if (user == null) {
            throw new WebApplicationException(404);
        }
        return user;
    }

    public User findById(UUID id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException(404));
    }
}
