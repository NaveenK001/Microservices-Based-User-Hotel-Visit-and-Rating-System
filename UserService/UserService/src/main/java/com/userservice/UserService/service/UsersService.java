package com.userservice.UserService.service;

import com.userservice.UserService.entities.Users;

import java.util.List;

public interface UsersService {


    Users saveUsers(Users users);


    List<Users> getAllUsers();


    Users getUsers(String userId);






}
