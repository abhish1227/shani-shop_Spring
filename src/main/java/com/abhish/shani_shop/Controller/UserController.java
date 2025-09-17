package com.abhish.shani_shop.Controller;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.abhish.shani_shop.dto.UserDto;
import com.abhish.shani_shop.enums.RoleType;
import com.abhish.shani_shop.exceptions.AlreadyExistsException;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.request.CreateUserRequest;
import com.abhish.shani_shop.request.LoginRequest;
import com.abhish.shani_shop.request.UpdateUserRequest;
import com.abhish.shani_shop.response.APIResponse;
import com.abhish.shani_shop.service.user.IUserService;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;
    private final ModelMapper modelMapper;

    // @Secured("ROLE_ADMIN") - we can also use this but a better way is to use
    // @PreAuthorize
    @PreAuthorize("hasRole('ADMIN')") // here we can define other constraints using AND, OR
    @GetMapping("/getAll")
    public ResponseEntity<APIResponse> getAllUsers() {
        return ResponseEntity.ok().body(new APIResponse("Users fetched successfully.",
                userService.getAllUsers()
                        .stream()
                        .map(user -> modelMapper.map(user, UserDto.class))
                        .toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<APIResponse> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok().body(new APIResponse("Users fetched successfully.", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<APIResponse> getUser() {
        try {
            User user = userService.getAuthenticatedUser();
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new APIResponse("Account details fetched successfully!", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/public/login")
    public ResponseEntity<APIResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok()
                .body(new APIResponse("Login successful.", userService.login(request)));
    }

    @PostMapping("/public/signup/admin")
    public ResponseEntity<APIResponse> createAdmin(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request, RoleType.ADMIN);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new APIResponse("Signup successful!", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/public/signup/customer")
    public ResponseEntity<APIResponse> createCustomer(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request, RoleType.CUSTOMER);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new APIResponse("Signup successful!", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/public/signup/seller")
    public ResponseEntity<APIResponse> createSeller(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request, RoleType.SELLER);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new APIResponse("Signup successful!", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<APIResponse> updateUser(@RequestBody UpdateUserRequest request) {
        try {

            User user = userService.updateUser(request, userService.getAuthenticatedUser());
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new APIResponse("Account details updated successfully!", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<APIResponse> deleteAccount() {
        try {
            User user = userService.getAuthenticatedUser();
            userService.deleteUser(user);
            return ResponseEntity.ok().body((new APIResponse("Your account has been deleted successfully!", null)));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<APIResponse> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body(new APIResponse("User account deleted successfully!", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }
}
