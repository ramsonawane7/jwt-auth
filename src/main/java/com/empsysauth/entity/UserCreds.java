package com.empsysauth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_creds")
@Data
public class UserCreds {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String passwordHash;
}
