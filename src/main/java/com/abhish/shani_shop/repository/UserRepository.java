package com.abhish.shani_shop.repository;

import java.util.Optional;

import com.abhish.shani_shop.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}
