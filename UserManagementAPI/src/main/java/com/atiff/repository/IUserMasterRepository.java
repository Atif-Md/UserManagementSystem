package com.atiff.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atiff.entity.UserMaster;


public interface IUserMasterRepository extends JpaRepository<UserMaster, Integer> {
	
	public UserMaster findByEmailAndPassword(String email, String password);
	public UserMaster findByNameAndEmail(String name, String email);

}
