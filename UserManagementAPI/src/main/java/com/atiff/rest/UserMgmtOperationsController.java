package com.atiff.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atiff.bindings.ActivateUser;
import com.atiff.bindings.LoginCredentials;
import com.atiff.bindings.RecoverPassword;
import com.atiff.bindings.UserAccount;
import com.atiff.service.IUserMgmtService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/user-api")
public class UserMgmtOperationsController {
	
	@Autowired
	private IUserMgmtService userService;
	
	@PostMapping("/save")
	public ResponseEntity<String> saveUser(@RequestBody UserAccount account) {
		//use service
		try {
			String resultMsg = userService.registerUser(account);
			return new ResponseEntity<String>(resultMsg, HttpStatus.CREATED);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/activate")
	public ResponseEntity<String> activateUser(@RequestBody ActivateUser user) {
		//use service
		try {
			String resultMsg = userService.activateUserAccount(user);
			return new ResponseEntity<String>(resultMsg, HttpStatus.CREATED);	
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> performLogin(@RequestBody LoginCredentials credentials) {
		try {
			//use service
			String resultMsg = userService.login(credentials);
			return new ResponseEntity<String>(resultMsg, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/report")
	public ResponseEntity<?> showUsers() {
		try {
			//use service
			List<UserAccount> listUsers = userService.listUsers();
			return new ResponseEntity<List<UserAccount>>(listUsers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/find/{id}")
	public ResponseEntity<?> showUserById(@PathVariable Integer id) {
		try {
			UserAccount account = userService.showUserByUserId(id);
			return new ResponseEntity<UserAccount>(account, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/find/{email}/{name}")
	public ResponseEntity<?> showUserByNameAndEmail(@PathVariable String email, @PathVariable String name) {
		try {
			UserAccount account = userService.showUserByEmailAndName(email, name);
			return new ResponseEntity<UserAccount>(account, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/update")
	public ResponseEntity<String> updateUserDetails(@RequestBody UserAccount account) {
		try {
			String user = userService.updateUser(account);
			return new ResponseEntity<String>(user, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteUserById(@PathVariable Integer id) {
		try {
			String string = userService.deleteUserById(id);
			return new ResponseEntity<String>(string, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PatchMapping("/changeStatus/{id}/{status}")
	public ResponseEntity<String> changeStatus(@PathVariable Integer id,
												@PathVariable String status) {
		try {
			String string = userService.changeUserStatus(id, status);
			return new ResponseEntity<String>(string, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/recover-password")
	public ResponseEntity<String> recoverPassword(@RequestBody RecoverPassword recover) {
		try {
			String recoverPassword = userService.recoverPassword(recover);
			return new ResponseEntity<String>(recoverPassword, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
