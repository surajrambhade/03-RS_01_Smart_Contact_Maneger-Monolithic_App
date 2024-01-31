package com.smart.controller;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import com.razorpay.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Integer Cid = null;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private MyOrderRepository myOrderRepository;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME " + userName);

		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER " + user);

		model.addAttribute("user", user);

	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");
		// get the user using username(email)
		return "normal/user_dashboard";
	}

	// open add form handler

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";

	}

	// procssing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);

			// processing img file

			if (file.isEmpty()) {
				// is the file is empty then try message
				System.out.println("No Img found !!");
				contact.setImage("contact.png");

			} else {
				// file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is upload");
			}
			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("DATA " + contact);
			System.out.println("Added to databass");

			// message success

			session.setAttribute("message", new Message("Your Contact is added !! Add More...", "success"));

		} catch (Exception e) {

			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();
			// message failed error
			session.setAttribute("message", new Message("Something went wrong !! try again...", "danger"));

		}
		return "normal/add_contact_form";
	}

	// show contact handler
	// per page = 5[n]
	// current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show User Contacts");

		/*
		 * contact ki list ko sned krna hai (ye ek approch hai) String userName
		 * =principal.getName(); User user = userRepository.getUserByUserName(userName);
		 * List<Contact> contacts = user.getContacts();
		 */

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		// currentPage-page
		// Contact Per-page - 5
		Pageable pageable = PageRequest.of(page, 10);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular showContactDetails

	@GetMapping("/{Cid}/contact")
	public String showContactDetails(@PathVariable("Cid") Integer Cid, Model model, Principal principal) {
		System.out.println("Cid " + Cid);
		Optional<Contact> contactOptional = this.contactRepository.findById(Cid);
		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// this is for bug fixing
		if (user.getId() == contact.getUser().getId()) {
			System.out.println("display ======************===");
			model.addAttribute("contact", contact);
		} else {
			System.out.println("Not display ===================");
		}

		return "normal/contact_detail";

	}

	// delete contact handler

	@GetMapping("/delete/{Cid}")
	public String deleteContact(@PathVariable("Cid") Integer Cid, Model model, Principal principal,
			HttpSession session) {
		// dono process ek hai niche
		// String userName = principal.getName();
		// User user = this.userRepository.getUserByUserName(userName);

		Contact contact = this.contactRepository.findById(Cid).get();

		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);

		// right hai 2
//		Optional<Contact> contactOptional = this.contactRepository.findById(Cid);
//		Contact contact = contactOptional.get();

		// 1 User maine contact ko cascade all kiya tha esiliya delete nhi hota , ek
		// trick unlink krna hoga

//		contact.setUser(null);
//		this.contactRepository.delete(contact);

		// contact.getImage()

		session.setAttribute("message", new Message("Contact deleted succesfully", "success"));

		// this is for bug fixing // upar contact null kiya esiliya niche db

		return "redirect:/user/show-contacts/0";
	}

	// open contact update form handler

	@PostMapping("/update-contact/{Cid}")
	public String updateForm(@PathVariable("Cid") Integer Cid, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(Cid).get();

		model.addAttribute("contact", contact);
		return "normal/update_form";

	}

	// update contact form handler

	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {

			// old contact details
			Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();

			// Img
			if (!file.isEmpty()) {
				// file work..
				// rewrite

				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();

				// update new photo

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContactDetail.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact Is Updated...", "success"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		System.out.println("CONTACT  NAME " + contact.getName());
		System.out.println("CONTACT ID " + contact.getCid());
		return "redirect:/user/" + contact.getCid() + "/contact";
	}

	// Your Profile

	@GetMapping("/profile")
	public String yourProfile(Model model) {

		model.addAttribute("title", "Profile Page");

		return "normal/profile";
	}

	// open setting handler

	@GetMapping("/settings")
	public String openSettings(Model model) {

		model.addAttribute("title", "Settings");

		return "normal/settings";
	}

	// change password

	@PostMapping("/change-password")
	public String changepassword(Model model, @RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		System.out.println("Old Password  " + oldPassword);
		System.out.println("New Password  " + newPassword);

		String UserName = principal.getName();

		User currenUser = this.userRepository.getUserByUserName(UserName);
		System.out.println(currenUser.getPassword());

		if (this.bCryptPasswordEncoder.matches(oldPassword, currenUser.getPassword())) {
			// change the password
			currenUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currenUser);
			session.setAttribute("message", new Message("Your password is successsfully change...", "success"));

		} else {
			// error
			session.setAttribute("message", new Message("Please enter correct old password !!", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}

	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data , Principal principal) throws Exception {

		System.out.println(data);
		int amt = Integer.parseInt(data.get("amount").toString());

		var client = new RazorpayClient(" sdthgfgxa = key dalo", "  sfghxax == secret key dalo");
		
		JSONObject ob = new JSONObject();
		ob.put("amount",amt*100);
		ob.put("currency","INR");
		ob.put("receipt", "txn_235425");
		
		// Creating new order
		
		Order order = client.orders.create(ob);
		System.out.println(order);
		
		// save the order in Db  --  table bnao , JPA se extend kro then below
		MyOrder myOrder = new MyOrder();
		
		myOrder.setAmount(order.get("amount")+" ");  // or yahan .toString() dalo = " " jgha
 		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		this.myOrderRepository.save(myOrder);
		
		
		// if you want save to DB
		return order.toString(); 

	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder( @RequestBody Map<String, Object> data) {
		
		MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		this.myOrderRepository.save(myOrder);
		
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
		
	}

}
