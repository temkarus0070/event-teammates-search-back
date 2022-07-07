package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Projections.UserProjection;
import org.netcracker.eventteammatessearch.Services.PayService;
import org.netcracker.eventteammatessearch.Services.UserService;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.entity.mongoDB.CommercialAccountConnectionTicket;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(value = "/api/users")
@RestController
public class UsersController {

    @Autowired
    UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayService payService;




    @PatchMapping("/updateUserPhoto")
    public void updateUserPhoto(@RequestBody User user) {
        userService.updateUserPhoto(user);
    }

    @PatchMapping("/deleteUserPhoto")
    public void deleteUserPhoto(@RequestBody User user) {
        userService.deleteUserPhoto(user);
    }

    @GetMapping("/getUsers")
    public List<User> getUsers() {
        List<User> all = userService.getAll();
        List<User> collect = all.stream().map(u -> new User(u.getAuthorities(), u.getLogin(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone(), u.getPictureUrl())).collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/getUserByLogin")
    public UserProjection getByLogin(@RequestParam String userLogin) {
        return userService.getUserByLogin(userLogin);
    }

    @GetMapping("/checkUser")
    public Object checkUser() {
        return 1;
    }

    @GetMapping("/usersListByLogin")
    public List<User> getUsersListByLogin(@RequestParam String login, Principal principal) {
        return userRepository.getUsersListByLoginWithValidation(login, principal.getName());
    }

    @GetMapping("/usersListByName")
    public List<User> getUsersListByName(@RequestParam String name, Principal principal) {
        String tempName[] = name.split(" ");
        System.out.println("\n\n\n\n\n" + tempName);
        if (tempName.length > 1)  return userRepository.getUsersByName(tempName[0], tempName[1], principal.getName());
        else return userRepository.getUsersByName(tempName[0], principal.getName());
    }

    @PatchMapping("/updateUser")
    public void updateUser(@RequestBody User user) {
        try{
            userService.updateUser(user);
        }
        catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());
        }
    }

    @PatchMapping("/updateUserAuthority")
    public void updateUserAuthority(@RequestBody User user) {
        userService.updateUserAuthority(user);
    }

    @PatchMapping("/updateUsersAuthorities")
    public void updateUsers(@RequestBody List<User> users){
            this.userService.updateUsers(users);
    }


    @GetMapping("/approvePassword")
    public boolean approvePassword(@RequestParam String login, @RequestParam String password) {
        return userService.approvePassword(login, password);
    }
    @GetMapping("/usersList")
    public List<User> get(HttpServletRequest request) {
        return userRepository.getUsersByLogin(request.getHeader("login"));
    }

    @GetMapping("/usersListByEmail")
    public List<User> getUsersByEmail(@RequestParam String mail) {
        return userRepository.getUsersByEmail(mail);
    }

    @GetMapping("/getExistingCommercialRegistration")
    public CommercialAccountConnectionTicket getExistingCommercialRegistration(Principal principal) {
        return payService.getExistingCommercialUserRegistration(principal);
    }

    @GetMapping("/getCurrentUser")
    public User getCurrentUser(Principal principal){
        UserProjection userByLogin = userService.getUserByLogin(principal.getName());
        return new User(userByLogin.getLogin(),userByLogin.getAuthorities());
    }

    @PatchMapping("/updateCommercialAcc")
    public void updateCommercialAcc(@RequestBody User user) {
        userService.updateCommercialAcc(user);
    }
}
