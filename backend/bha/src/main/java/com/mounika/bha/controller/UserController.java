package com.mounika.bha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mounika.bha.dto.LoginDTO;
import com.mounika.bha.dto.UserDto;
import com.mounika.bha.entity.User;
import com.mounika.bha.repository.UserRepository;
import com.mounika.bha.response.LoginResponse;
import com.mounika.bha.service.UserService;
//import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpSession;




@RestController
@CrossOrigin
public class UserController {
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private UserRepository userRepo;
	
	@PostMapping(path = "/registration")
	public String saveUser(@RequestBody UserDto userDto) {
		userDto.setRole("user");
		userDto.setRole(userDto.getRole());
		userService.addUser(userDto);
		return "login";
	}
	
	@PostMapping(path = "/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
		//System.out.println("Working");
		
		//session.setAttribute("userEmail", loginDTO.getEmail());
		LoginResponse loginResponse = userService.loginUser(loginDTO);
		return ResponseEntity.ok(loginResponse);
	}
	
	@PostMapping("/authenticate")
    public ResponseEntity<?> loginAdmin(@RequestBody User user) {
    	//session.setAttribute("adminEmail", user.getEmail());
    	LoginResponse loginResponse = userService.loginAdmin(user);
		return ResponseEntity.ok(loginResponse);
    }
 
	

}
