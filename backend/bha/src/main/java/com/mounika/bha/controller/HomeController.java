package com.mounika.bha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.mounika.bha.repository.UserRepository;
import com.mounika.bha.service.UserService;

@Controller
public class HomeController {
	
	@Autowired
    private UserRepository userRepo;
	
	@Autowired
	private UserService userService;

	
	@GetMapping("/register")
	public String displaySignup() {
        return "registration";
    }
	
	@GetMapping("/login")
	public String displayLogin() {
        return "login";
    }
	@GetMapping("/userdash1")
	public String displayUser() {
        return "userdash";
    }
	@GetMapping("/abc1")
	public String displayAbc() {
        return "abc";
    }
}
