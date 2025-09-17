package com.abhish.shani_shop.service.user;

import java.util.List;

import com.abhish.shani_shop.dto.LoginResponseDto;
import com.abhish.shani_shop.dto.UserDto;
import com.abhish.shani_shop.enums.RoleType;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.request.CreateUserRequest;
import com.abhish.shani_shop.request.LoginRequest;
import com.abhish.shani_shop.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);

    List<User> getAllUsers();

    User createUser(CreateUserRequest request, RoleType role);

    User updateUser(UpdateUserRequest request, User user);

    void deleteUser(User user);

    void deleteUserById(Long id);

    UserDto convertUserToDto(User user);

    LoginResponseDto login(LoginRequest request);

    User getAuthenticatedUser();
}
