package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.services.EmailService;

@Controller
public class ForgotController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	Random random = new Random(1000);
	// email id form handler

	@GetMapping("/forgot")
	public String openEmailForm() {

		return "forgot_email_from";

	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {

		System.out.println("Email  " + email);

		// generating otp 4 digit

		int otp = random.nextInt(9999999);
		System.out.println("OTP  " + otp);

		// write code for otp send to email
		String subject = "OTP From SCM";
		String message = "  "
				+ "<div style='border:1ParsedPersistenceXmlDescriptor soliDescriptor #e2e2e2; padding:20px'>" + "<h1>"
				+ "OTP is" + "<b>" + otp + "/b>" + "</h1>" + "</div>";

		String to = email;

		boolean flag = this.emailService.sendEamil(subject, message, to);

		if (flag) {
			session.setAttribute("Session_otp", otp);
			session.setAttribute("Session_email", email);

			return "/verify_otp";
		} else {

			session.setAttribute("message", "Check your mail id !!");
			return "/forgot_email_from";
		}

	}

	// verify OTP

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {

		int Session_otp = (Integer) session.getAttribute("Session_otp");
		String email = (String) session.getAttribute("Session_email");

		if (Session_otp == otp) {
			// password change form

			User user = this.userRepository.getUserByUserName(email);

			if (user == null) {
				// send error message

				session.setAttribute("message", "User does not exits with this email !!");
				return "/forgot_email_from";

			} else {
				// send change password form
			}
			return "password_change_form";

		} else {
			session.setAttribute("message", "You have enter wrong otp !!");
			return "/verify_otp";
		}
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword , HttpSession session) {
		
		String email= (String) session.getAttribute("Session_email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password changed successfully...";
		
		

	}
}
