package org.netcracker.eventteammatessearch.Services;

import org.hibernate.ObjectNotFoundException;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean checkIfPhoneNotUsed(String phone) {
        List<User> usersByPhone = userRepository.findUsersByPhone(phone);
        if (usersByPhone.size() == 0)
            return true;
        return false;
    }

    public void setPhone(Principal principal, String phone) {
        Optional<User> user = userRepository.findById(principal.getName());
        user.ifPresent(u -> {
            boolean checkIfPhoneNotUsed = checkIfPhoneNotUsed(phone);
            if (!checkIfPhoneNotUsed) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Данный  номер телефона уже занят");
            } else u.setPhone(phone);
            //  userRepository.save(u);
        });
    }

    public void setPhoneConfirmed(Principal principal) {
        User userByLogin = getUserByLogin(principal.getName());
        if (userByLogin != null) {
            userByLogin.setPhoneConfirmed(true);
            userRepository.save(userByLogin);

        } else throw new ObjectNotFoundException(principal.getName(), "User");
    }


    public User getUserByLogin(String login) {
        User user = userRepository.findById(login).orElse(null);
        return new User(user.getAuthorities(),user.getLogin(),user.getFirstName(),user.getLastName()
        ,user.getEmail(),user.getPhone(),user.getPictureUrl(),user.isOauthUser());
    }

    public List<User> getAll() {
        List<User> all = userRepository.findAllUsers();
        return all;
    }

    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getLogin()).orElse(null);
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        if (!user.getPhone().equals(existingUser.getPhone())&&user.getPhone()!=null&&!user.getPhone().equals("")){
            User userByPhone = userRepository.findUserByPhoneContains(user.getPhone());
            if (userByPhone!=null){
                throw new Error("данный номер телефона уже занят другим пользователем");
            }
        }
        existingUser.setPhone(user.getPhone());
        existingUser.setPictureUrl(user.getPictureUrl());
        existingUser.setOrganizationName(user.getOrganizationName());
        existingUser.setDescription(user.getDescription());
        existingUser.setCommercialUser(user.isCommercialUser());
        existingUser.setCommercialUserCreated(user.isCommercialUserCreated());
        existingUser.setOrganizationName(user.getOrganizationName());
        existingUser.setDescription(user.getDescription());
        existingUser.setCommercialUser(user.isCommercialUser());
        existingUser.setCommercialUserCreated(user.isCommercialUserCreated());

        String newPassword = passwordEncoder.encode(user.getPassword());
        existingUser.setPassword(newPassword);
        userRepository.save(existingUser);
    }

    public void updateUserAuthority(User user){
        this.userRepository.findById(user.getLogin()).ifPresent(e-> {
            e.setAuthorities(user.getAuthorities());
            this.userRepository.save(e);
        });
    }
    public void updateUsers(List<User> users){
        Map<String, User> userMap = userRepository.findAllById(users.stream().map(User::getLogin).collect(Collectors.toList())).stream().collect(Collectors.<User, String, User>toMap(User::getLogin, e -> e));
        if (userMap.size()!=users.size()){
            throw new Error("Вы пытаетесь обновить несуществующую сущность");
        }
        users.forEach(e->{
            userMap.get(e.getLogin()).setAuthorities(e.getAuthorities());
        });
        userRepository.saveAll(userMap.values());
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


    public boolean approvePassword(String login, String password) {
        User existingUser = userRepository.findById(login).orElse(null);

        boolean result = passwordEncoder.matches(password, existingUser.getPassword());
        return result;
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
}
