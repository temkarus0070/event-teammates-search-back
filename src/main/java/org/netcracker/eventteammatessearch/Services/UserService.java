package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    public boolean approvePassword(String login, String password) {
        User existingUser = userRepository.findById(login).orElse(null);
        String encodedPassword = passwordEncoder.encode(password);

        System.out.println("encoded pass from req = " + encodedPassword);
        System.out.println("encoded pass from db = " + existingUser.getPassword());

        boolean result = encodedPassword.equals(existingUser.getPassword());
        return result;
    }
}
