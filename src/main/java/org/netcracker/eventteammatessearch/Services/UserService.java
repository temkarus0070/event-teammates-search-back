package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByLogin(String login) {
        return userRepository.findById(login).orElse(null);
    }

    public List<User> getAll() {
        List<User> all = userRepository.findAll();
        return all;
    }

    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getLogin()).orElse(null);
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());

        String newPassword = passwordEncoder.encode(user.getPassword());
        existingUser.setPassword(newPassword);

        userRepository.save(existingUser);
    }
}
