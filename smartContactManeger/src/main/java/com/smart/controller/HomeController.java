package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	
	// This is home handler 
	@RequestMapping("/")
	private String home(Model model) {
		
		model.addAttribute("title","Home-Smart Contact manager");
		
		
		return "home";
	}
	
	// This is about handler 
	@RequestMapping("/about")
	private String about(Model model) {
		
		model.addAttribute("title","About-Smart Contact manager");
		
		return "about";
	}
	
	
	// This is signup handler 
	@RequestMapping("/signup")
	private String signup(Model model) {
		
		model.addAttribute("title","Register-Smart Contact manager");
		model.addAttribute("user", new User());
		
		return "signup";
	}
	
	// This handler is for registering user  
	//@PostMapping("/do_register") //easy way
	@RequestMapping(value = "/do_register" , method= RequestMethod.POST )
	public String registerUser(@Valid @ModelAttribute("user") User user ,  BindingResult result1,
			@RequestParam(value = "agreement" , defaultValue = "false") boolean agreement, 
			Model model , HttpSession session) {
		
		try {
			if(!agreement) {
				System.out.println("You have not agreed the T & C");
				throw new Exception("You have not agreed the T & C");
			}
			
			if(result1.hasErrors()) {
				System.out.println("ERROR " + result1.toString() );
				model.addAttribute("user", user);
				
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("agreement " +agreement);
			System.out.println("USER " + user );
			
			User result = this.userRepository.save(user);
			
			
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered !!" ,"alert-success" )  );
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Worng !!" + e.getMessage() ,"alert-danger" )  );
			return "signup";
		
		}
	}
	
	//handler for costom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title", "Login Page");
		
		
		return "login";
	}
	
	
}

/*
 * 
 * @Autowired private UserRepository userRepository;
 * 
 * 
 * @GetMapping("/test")
 * 
 * @ResponseBody public String test() { System.out.println("----------------");
 * User user = new User(); user.setName("suraj");
 * user.setEmail("ghgtyu@gmail.com"); userRepository.save(user); return
 * "Working"; }
 */