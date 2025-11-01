package com.empsysauth.repository;

import com.empsysauth.entity.UserCreds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredsRepository extends JpaRepository<UserCreds, Long> {
	UserCreds findByUsername(String username);
}