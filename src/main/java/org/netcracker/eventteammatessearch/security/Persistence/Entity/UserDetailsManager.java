package org.netcracker.eventteammatessearch.security.Persistence.Entity;

import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserDetailsManager implements org.springframework.security.provisioning.UserDetailsManager {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createUser(UserDetails user) {
       org.netcracker.eventteammatessearch.security.Entity.UserDetails userDetails = (org.netcracker.eventteammatessearch.security.Entity.UserDetails) user;
        userDetails.getUser().setPassword(passwordEncoder.encode(userDetails.getPassword()));
        if (userRepository.findById(userDetails.getUsername()).isEmpty())
            userRepository.save(userDetails.getUser());
        else throw new AuthorizationServiceException("username is already used");

    }

    @Override
    public void updateUser(UserDetails user) {
        userRepository.update(user.getAuthorities().parallelStream().collect(Collectors.toList()), user.getUsername(), user.getPassword());
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
    }


    public void changePassword(String oldPassword, String username, String newPassword) throws org.springframework.security.access.AuthorizationServiceException {
        String oldPass = passwordEncoder.encode(oldPassword);
        String newPass = passwordEncoder.encode(newPassword);
        Optional<User> user = userRepository.findById(username);
        if (user.isPresent()) {
            User userEntity = user.get();
            if (userEntity.getPassword().equals(oldPass)) {
                userEntity.setPassword(newPass);
                userRepository.updatePassword(userEntity.getPassword(), userEntity.getLogin());
            } else throw new AuthorizationServiceException("Invalid old pass");
        }
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findById(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userEntityOptional = userRepository.findById(username);
        if (userEntityOptional.isPresent())
            return new org.netcracker.eventteammatessearch.security.Entity.UserDetails(userEntityOptional.get());
        else throw new UsernameNotFoundException("User with such username have not  found");
    }
}
