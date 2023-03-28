package com.concertidc.mcqtest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concertidc.mcqtest.model.ERole;
import com.concertidc.mcqtest.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

	Optional<Role> findByName(ERole name);
}
