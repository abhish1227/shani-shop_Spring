package com.abhish.shani_shop.service.user;

import java.util.List;
import java.util.Optional;

import com.abhish.shani_shop.dto.LoginResponseDto;
import com.abhish.shani_shop.dto.UserDto;
import com.abhish.shani_shop.enums.RoleType;
import com.abhish.shani_shop.exceptions.AlreadyExistsException;
import com.abhish.shani_shop.exceptions.ResourceNotFoundException;
import com.abhish.shani_shop.model.User;
import com.abhish.shani_shop.repository.UserRepository;
import com.abhish.shani_shop.request.CreateUserRequest;
import com.abhish.shani_shop.request.LoginRequest;
import com.abhish.shani_shop.request.UpdateUserRequest;
import com.abhish.shani_shop.security.AuthUtil;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Please enter a valid user id."));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public LoginResponseDto login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);

        return new LoginResponseDto(token, user.getId());
    }

    @Override
    public User createUser(CreateUserRequest request, RoleType role) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.getRoles().add(role);
                    return userRepository.save(user);
                }).orElseThrow(() -> new AlreadyExistsException("Can't create user as the requested user email Id: "
                        + request.getEmail() + " already exists!"));
    }

    @Transactional
    @Override
    public User updateUser(UpdateUserRequest request, User existingUser) {
        if (request.getFirstName() != null)
            existingUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            existingUser.setLastName(request.getLastName());
        return userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No user with the given id."));
        userRepository.delete(user);
    }

    @Override
    public UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // this fetches only the
                                                                                                // information available
                                                                                                // in the
                                                                                                // securityContext, i.e.
                                                                                                // email, roles,
                                                                                                // UserDetails object.
        String email = authentication.getName(); // when the email is fetched we then find the user using the emailId.
        return userRepository.findByEmail(email).orElseThrow();
    }
}
