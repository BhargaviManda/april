package com.mounika.bha.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mounika.bha.dto.LoginDTO;
import com.mounika.bha.dto.UserDto;
import com.mounika.bha.entity.User;
import com.mounika.bha.repository.UserRepository;
import com.mounika.bha.response.LoginResponse;

@Service
public class UserService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private UserRepository userRepo;
	
	 public String addUser(UserDto userDTO) {
    	 User user = userRepo.findByEmail(userDTO.getEmail());
         if (user == null) {
        	 user = new User();  // Add this line to create a new User object
         }
         
        
    	
        //User user = new User();
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setConfirmpassword(passwordEncoder.encode(userDTO.getConfirmpassword()));
        user.setPhonenumber(userDTO.getPhonenumber());
        user.setAddress(userDTO.getAddress());
        user.setPincode(userDTO.getPincode());
        user.setRole(userDTO.getRole());
        userRepo.save(user);
        return user.getFirstname();
    }
    
	 public LoginResponse loginUser(LoginDTO loginDTO) {
	        User user1 = userRepo.findByEmail(loginDTO.getEmail());
	        if (user1 != null && user1.getRole().equals("user")) {
	            String password = loginDTO.getPassword();
	            String encodedPassword = user1.getPassword();
	            Boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
	            if (isPwdRight) {
	                Optional<User> user = userRepo.findOneByEmailAndPassword(loginDTO.getEmail(), encodedPassword);
	                if (user.isPresent()) {
	                    return new LoginResponse("Login Success", true); // Fixed syntax
	                } else {
	                    return new LoginResponse("Login Failed", false);
	                }
	            } else {
	                return new LoginResponse("Password Not Match", false); // Fixed typo
	            }
	        } else {
	            return new LoginResponse("Email not exists", false);
	        }
	        
	    }
	 public LoginResponse loginAdmin(User user) {
			User user2 = userRepo.findByEmail(user.getEmail());
	        if (user2 != null && user2.getRole().equals("admin")) {
	            String password = user.getPassword();
	            String encodedPassword = user2.getPassword();
//	            System.out.println(password);
	            Boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
	            if (isPwdRight) {
	                Optional<User> adm = userRepo.findOneByEmailAndPassword(user.getEmail(), encodedPassword);
	                if (adm.isPresent()){
	                    return new LoginResponse("Login Success", true); // Fixed syntax
	                } else {
	                    return new LoginResponse("Login Failed", false);
	                }
	            } else {
	                return new LoginResponse("Password Not Match", false); // Fixed typo
	            }
	        } else {
	            return new LoginResponse("Email not exists", false);
	        }
		}
}
