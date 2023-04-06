package com.concertidc.mcqtest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concertidc.mcqtest.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	List<Department> findByDepartmentCode(String departmentCode);
}
