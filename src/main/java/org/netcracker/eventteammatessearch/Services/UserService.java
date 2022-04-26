package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
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
        User user = userRepository.findById(login).orElse(null);
        return user;
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
        existingUser.setPictureUrl(user.getPictureUrl());
        existingUser.setOrganizationName(user.getOrganizationName());
        existingUser.setDescription(user.getDescription());
        existingUser.setCommercialUser(user.isCommercialUser());
        existingUser.setCommercialUserCreated(user.isCommercialUserCreated());

        String newPassword = passwordEncoder.encode(user.getPassword());
        existingUser.setPassword(newPassword);

        userRepository.save(existingUser);
    }

    public void updateUserPhoto(User user) {
        User existingUser = userRepository.findById(user.getLogin()).orElse(null);
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setOrganizationName(user.getOrganizationName());
        existingUser.setDescription(user.getDescription());
        existingUser.setCommercialUser(user.isCommercialUser());
        existingUser.setCommercialUserCreated(user.isCommercialUserCreated());

        existingUser.setPictureUrl(user.getPictureUrl());

        userRepository.save(existingUser);
    }

    public void deleteUserPhoto(User user) {
        User existingUser = userRepository.findById(user.getLogin()).orElse(null);
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setOrganizationName(user.getOrganizationName());
        existingUser.setDescription(user.getDescription());
        existingUser.setCommercialUser(user.isCommercialUser());
        existingUser.setCommercialUserCreated(user.isCommercialUserCreated());

        existingUser.setPictureUrl(null);

        userRepository.save(existingUser);
    }

    public void updateCommercialAcc(User user) {
        User existingUser = userRepository.findById(user.getLogin()).orElse(null);
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setPictureUrl(user.getPictureUrl());
        existingUser.setOrganizationName(user.getOrganizationName());
        existingUser.setDescription(user.getDescription());
        existingUser.setCommercialUser(user.isCommercialUser());
        existingUser.setCommercialUserCreated(user.isCommercialUserCreated());

        userRepository.save(existingUser);
    }

    public boolean approvePassword(String login, String password) {
        User existingUser = userRepository.findById(login).orElse(null);

        boolean result = passwordEncoder.matches(password, existingUser.getPassword());
        return result;
    }
}
