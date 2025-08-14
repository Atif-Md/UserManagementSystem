package com.atiff.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.atiff.bindings.ActivateUser;
import com.atiff.bindings.LoginCredentials;
import com.atiff.bindings.RecoverPassword;
import com.atiff.bindings.UserAccount;
import com.atiff.entity.UserMaster;
import com.atiff.repository.IUserMasterRepository;
import com.atiff.utils.EmailUtils;

@Service
public class UserMgmtServiceImpl implements IUserMgmtService {
	
	@Autowired
	private IUserMasterRepository userMasterRepo;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private Environment env;

	@Override
	public String registerUser(UserAccount user) {
		// convert UserAccount obj data to UserMaster(Entity obj) obj data
		UserMaster master = new UserMaster();
		BeanUtils.copyProperties(user, master);
		//set random string of 6 chars as password
		String tempPwd = generateRandomPassword(6);
		master.setPassword(tempPwd);
		master.setActive_sw("InActive");
		//save the obj
		UserMaster savedMaster = userMasterRepo.save(master);
		// send the mail
		String subject = "User Registration Success";
		String body = readEmailMessageBody(env.getProperty("mailbody.registeruser.location"), user.getName(), tempPwd);
		emailUtils.sendEmailMsg(user.getEmail(), subject, body);
		
		//return the msg
		return savedMaster!=null ? "User is registered with id value "+master.getUserId()+
									"check mail for temporary password"
									: "Problem in user registration";
	}

	@Override
	public String activateUserAccount(ActivateUser user) {
		// convert ActivateUser obj to UserMaster(Entity obj)
		UserMaster master = new UserMaster();
		master.setEmail(user.getEmail());
		master.setPassword(user.getTempPassword());
		//check the record availability using email and tempPassword
		Example<UserMaster> example = Example.of(master);
		List<UserMaster> list = userMasterRepo.findAll(example);
		
		// if valid email and tempPassword given then set endUser supplied real password to update the record
		if(list!=null) {
			// get entity obj
			UserMaster entity = list.get(0);
			//set the password
			entity.setPassword(user.getNewPassword());
			// change the userAccount status to active
			entity.setActive_sw("Active");
			//update the obj
			userMasterRepo.save(entity);
			return "User is activated with new password";
		}
		return "User is not found for activation";
	}

	@Override
	public String login(LoginCredentials credentials) {
		// convert LoginCredentials obj to UserMaster obj(Entity obj)
		UserMaster master = new UserMaster();
		BeanUtils.copyProperties(credentials, master);
		// prepare example obj
		Example<UserMaster> example = Example.of(master);
		List<UserMaster> listEntities = userMasterRepo.findAll(example);
		if(listEntities.size()==0)
			return "Invalid Credentials";
		else {
			// get entity obj
			UserMaster entity = listEntities.get(0);
			if(entity.getActive_sw().equalsIgnoreCase("Active")) {
				return "Valid credentials and login successful";
			} else {
				return "User account is not active";
			}
		}
	}

	@Override
	public List<UserAccount> listUsers() {
		// Load all entities and convert to UserAccount obj
		return userMasterRepo.findAll().stream().map(entity->{
			UserAccount user = new UserAccount();
			BeanUtils.copyProperties(entity, user);
			return user;
		}).toList();
		
		
		// Load all entities and convert to UserAccount obj
		/*List<UserMaster> list = userMasterRepo.findAll();
		List<UserAccount> listUsers = list.stream().map(entity->{
			UserAccount user = new UserAccount();
			BeanUtils.copyProperties(entity, user);
			return user;
		}).toList();
		return listUsers;*/
		
		
		// convert all entities to UserAccount obj
	 /* List<UserAccount> listUsers = new ArrayList<UserAccount>();
		list.forEach(entity->{
			UserAccount user = new UserAccount();
			BeanUtils.copyProperties(entity, user);
			listUsers.add(user);
		});
		return listUsers; */
		
	}

	@Override
	public UserAccount showUserByUserId(Integer id) {
		// load the user by user id
		Optional<UserMaster> opt = userMasterRepo.findById(id);
		if(opt.isPresent()) {
			UserAccount account = new UserAccount();
			BeanUtils.copyProperties(opt.get(), account);
			return account;
		}
		return null;
	}

	@Override
	public UserAccount showUserByEmailAndName(String email, String name) {
		//use the custom findBy(-) method
		UserMaster master = userMasterRepo.findByNameAndEmail(name, email);
		UserAccount account = null;
		if(master!=null) {
			account = new UserAccount();
			BeanUtils.copyProperties(master, account);
		}
		return account;
	}

	@Override
	public String updateUser(UserAccount user) {
		// use the custom findBy(-) method
		Optional<UserMaster> optional = userMasterRepo.findById(user.getUserId());
		if(optional!=null) {
			// get entity obj
			UserMaster master = optional.get();
			BeanUtils.copyProperties(user, master);
			userMasterRepo.save(master);
			return "User Details are Updated";
		} else {
			return "User Not found for updation";
		}
	}

	@Override
	public String deleteUserById(Integer id) {
		// load the obj
		Optional<UserMaster> optional = userMasterRepo.findById(id);
		if(optional.isPresent()) {
			userMasterRepo.deleteById(id);
			return "User is deleted";
		}
		return "User is not found for deletion";
	}

	@Override
	public String changeUserStatus(Integer id, String status) {
		// load the obj
		Optional<UserMaster> optional = userMasterRepo.findById(id);
		if(optional.isPresent()) {
			// get entity obj
			UserMaster master = optional.get();
			// change the status
			master.setActive_sw(status);
			// update the obj
			userMasterRepo.save(master);
			return "user status changed";
		}
		return "user not found for changing the status";
	}

	@Override
	public String recoverPassword(RecoverPassword recover) {
		// get UserMaster obj(Entity obj) by name and email
		UserMaster master = userMasterRepo.findByNameAndEmail(recover.getName(), recover.getEmail());
		if(master!=null) {
			String pwd = master.getPassword();
			// sent the recovered password to email account
			String subject = "Mail for password recovery";
			String mailBody = readEmailMessageBody(env.getProperty("mailbody.recoverpwd.location"), recover.getName(), pwd);
			emailUtils.sendEmailMsg(recover.getEmail(), subject, mailBody);
			
			return "Email is sent having the recovered password";
		}
		return "User and Email is not found";
	}
	
	// helper method for same class
	private String generateRandomPassword(int length) {
		// a list of chars to choose from in form of string
		String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		// creating a StringBuffer of size length
		StringBuilder randomWord = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			// generating random number using math.random() (gives pseudo number 0.0 to 1.0)
			int ch = (int) (string.length() * Math.random());
			//adding random character one by one at the end of randomWord
			randomWord.append(string.charAt(ch));
		}
		return randomWord.toString();
	}
	
	private String readEmailMessageBody(String fileName, String fullName, String tempPassword) {
		
		String mailBody=null;
		String url="http://localhost:4041/user-api/activate";
		try(FileReader reader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(reader);) {
			// read file content to StringBuffer obj line by line
			StringBuffer buffer = new StringBuffer();
			String line = null;
			do {
				line = br.readLine();
				if(line!=null)
					buffer.append(line);
			} while (line!=null);
			mailBody = buffer.toString();
			mailBody = mailBody.replace("{FULL-NAME}", fullName);
			mailBody = mailBody.replace("{PWD}", tempPassword);
			mailBody = mailBody.replace("{URL}", url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mailBody;
	}

}
