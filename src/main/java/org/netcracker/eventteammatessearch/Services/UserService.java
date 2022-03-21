package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getUserByToken(String token) {
        User user = userRepository.findByPassword(token);
        return user;
    }

    public Optional<User> getUserByLogin(String login) {
        Optional<User> user = userRepository.findById(login);
        return user;
    }

    public List<User> getAll() {
        List<User> all = userRepository.findAll();
        return all;
    }
}
