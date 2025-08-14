package com.atiff.service;

import java.util.List;

import com.atiff.bindings.ActivateUser;
import com.atiff.bindings.LoginCredentials;
import com.atiff.bindings.RecoverPassword;
import com.atiff.bindings.UserAccount;

public interface IUserMgmtService {
	
	public String registerUser(UserAccount user);
	public String activateUserAccount(ActivateUser user);
	public String login(LoginCredentials credentials);
	public List<UserAccount> listUsers();
	public UserAccount showUserByUserId(Integer id);
	public UserAccount showUserByEmailAndName(String email, String name);
	public String updateUser(UserAccount user);
	public String deleteUserById(Integer id);
	public String changeUserStatus(Integer id, String status);
	public String recoverPassword(RecoverPassword recover);
	

}
