package com.smartcontact.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.deo.UserRepository;
import com.smartcontact.entity.User;
import com.smartcontact.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class controller {


	@Autowired
	private UserRepository userrepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/home")
	public String homePage(Model m)
	{
		m.addAttribute("title", "smart-contact-manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String aboutPage(Model m)
	{
		m.addAttribute("title", "smart-contact-manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signupPage(Model m)
	{
		m.addAttribute("title", "smart-contact-manager");
		m.addAttribute("user",new User());
		return "signup";
	}
	
	//handler register page
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user
								,BindingResult result,@RequestParam(value="agreement"
								,defaultValue ="false") boolean agreement
								,Model model,HttpSession session)
	{
		
		try {
			
			if(!agreement)
			{
		
				throw new Exception("agreement not accepted");
			}
			
			
			if(result.hasErrors())
			{
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			
			
			User result1= userrepository.save(user);
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("successfully Registered !!", "alert-success"));
			
			return "signup";
			
		} catch (Exception e) {
			
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!", "alert-danger"));
			return "signup";
			
		}
		
		
	}
	
	@GetMapping("/signin")
	public String signInPage(Model model)
	{
		model.addAttribute("title", "smart-contact-manager");
		return "login";
	}
	
}
