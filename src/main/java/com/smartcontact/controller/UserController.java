package com.smartcontact.controller;

import java.security.Principal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.deo.ContactRepository;
import com.smartcontact.deo.UserRepository;
import com.smartcontact.entity.User;
import com.smartcontact.entity.contact;
import com.smartcontact.helper.Message;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
  	private UserRepository userrepository;
	
	@Autowired
	private ContactRepository contactrepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	//common data of user
	
	@ModelAttribute
	public void commonInfo(Model model,Principal principal)
	{
		
		String username=principal.getName();
		User user=userrepository.getUserByUserName(username);
		model.addAttribute("user",user);
	}
	
	
	//user home 
	@RequestMapping("/index")
	public String userDashboard(Model model,Principal principal)
	{
		model.addAttribute("title","user-dashboard");
		return "normal/user_dashboard";
	}
	
	
	//show add contact form
	@GetMapping("/add-contact")
	public String openAddContactPage(Model model) 
	{
		model.addAttribute("title","add-newcontact");
		model.addAttribute("contact", new contact());
		return "normal/add_newcontact";
	}
	
	
	//saving contact details in database
	@PostMapping("/process-contact")
	public String addContact(
			@ModelAttribute contact con,
			Principal pricipal,
			HttpSession session)
	{
		try {
			String name=pricipal.getName();
			User user=userrepository.getUserByUserName(name);
			
			//add user foreign key in  in contact table
			con.setUs(user);
			
			
			//add contact in user table
			user.getList().add(con);
			
			userrepository.save(user);
			
			//message after successfully add added in contact 
			session.setAttribute("message", new Message("Your contact is added !! Add more..","success"));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong,Try again..","danger"));
		}
		
		
		
		return "normal/add_newcontact";
	}
	
	//showing contact details saved in database
	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page")Integer page ,Model model,Principal principal )
	{
		model.addAttribute("title", "Show Contact");
		
		String username= principal.getName();
		 User user= userrepository.getUserByUserName(username);
		 
		 //current Page No
		 //Contact per page
		 //add these 2 things in Pageble object
		 
		 Pageable pageble=PageRequest.of(page,2);
		 
		 //A page is a sublist of a list of objects
		 Page<contact> contacts= contactrepository.getByUserId(user.getId(),pageble);
		 
		 
		model.addAttribute("contact",contacts );
		model.addAttribute("currentPage",page);
		model.addAttribute("totalpages",contacts.getTotalPages());
		
		
		return "normal/show_contacts";
	}
	
	//show particular contact detail
	@GetMapping("/{cid}/contact")
	public String singleContactDetail(@PathVariable("cid")Integer cid,Model model,Principal principal)
	{
		
		 Optional<contact> optional = contactrepository.findById(cid);
		 contact contact = optional.get();
		 String username = principal.getName();
		 User user = userrepository.getUserByUserName(username);
		 
		 //check that user can watch on his own data, don't access other user data through url
		
		 if (user.getId()==contact.getUs().getId()) 
		 model.addAttribute("contact",contact);
		 model.addAttribute("title", contact.getName());
		
		 
		 return "normal/contact_detail";
	}
	
	
	
	//delete contact from database
	@GetMapping("/delete/{cid}")
	public String deleteconatct(@PathVariable("cid")Integer cid,Model model,HttpSession session)
	{
		
		Optional<contact> contactOptional = this.contactrepository.findById(cid);
		contact contact =contactOptional.get();
		
		
		contactrepository.delete(contact);
		session.setAttribute("message", new Message("contact delete successfully","success"));
		
		return "redirect:/user/show-contact/0";
	}
	
	
	
	//open update contact form 
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") int cid, Model m)
	{
		
		m.addAttribute("title", "contact update page");
		contact contact = contactrepository.findById(cid).get();
		m.addAttribute("contact", contact);
		
		return "normal/update_contact";
	}
	
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateProcess(@ModelAttribute contact con, Model m,Principal principal,HttpSession session )
	{
		
		try {
			
			String username= principal.getName();
			
			User user = userrepository.getUserByUserName(username);
			con.setUs(user);
			contactrepository.save(con);
			
			session.setAttribute("message",new Message("Your contact is updated","success"));
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		//m.addAttribute("message", "Contact updated successfully");
		return "redirect:/user/"+con.getCid()+"/contact";
	}
	
	@GetMapping("/profile")
	public String profilePage(Model m)
	{
		m.addAttribute("title", "user profile page");
		return "normal/profile";
	}
	
	@GetMapping("/setting")
	public String settingPage()
	{
		return "normal/setting";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpassword")String oldpassword,
								 @RequestParam("newpassword")String newpassword,
								 Principal principal, HttpSession session)
	{
		String username = principal.getName();
		User currentUser = userrepository.getUserByUserName(username);
		if(bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword()))
		{
			//encode new password and save
			currentUser.setPassword(bCryptPasswordEncoder.encode(newpassword));
			userrepository.save(currentUser);
			session.setAttribute("message", new Message("Your password is successfullu changed", "success"));
		}
		else {
			session.setAttribute("message", new Message("Please enter correct old password !!", "danger"));
			return "redirect:/user/setting";
		}
		
		return "redirect:/user/index";
	}
	
	
	
	

}
