package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.deo.ContactRepository;
import com.smartcontact.deo.UserRepository;
import com.smartcontact.entity.User;
import com.smartcontact.entity.contact;


  @RestController 
  public class SearchController {
  
  @Autowired 
  private UserRepository userrepository;
  
  @Autowired 
  private ContactRepository contactrepository;
  
  @GetMapping("/search/{query}") 
  public ResponseEntity<?>search(@PathVariable("query")String query,Principal principal) 
  {
	 
	  User user =userrepository.getUserByUserName(principal.getName());
			  		
  
  List<contact> contacts = contactrepository.findByNameContainingAndUs(query,user);
  
  return ResponseEntity.ok(contacts);
  
  }
  
  
  
  }
 