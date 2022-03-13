package org.netcracker.eventteammatessearch.security.Persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserDetailsManager implements org.springframework.security.provisioning.UserDetailsManager {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails user) {
        userRepository.save(new UserEntity(user.getUsername(), user.getPassword(), user.getAuthorities().parallelStream().collect(Collectors.toList())));
    }

    @Override
    public void updateUser(UserDetails user) {
        userRepository.update(new UserEntity(user.getUsername(), user.getPassword(), user.getAuthorities().parallelStream().collect(Collectors.toList())));
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
        Optional<UserEntity> user = userRepository.findById(username);
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            if (userEntity.getPassword().equals(oldPass)) {
                userEntity.setPassword(newPass);
                userRepository.update(userEntity);
            } else throw new AuthorizationServiceException("Invalid old pass");
        }
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findById(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findById(username);
        if (userEntityOptional.isPresent())
            return new org.netcracker.eventteammatessearch.security.UserDetails(userEntityOptional.get());
        else throw new UsernameNotFoundException("User with such username have not  found");
    }
}
