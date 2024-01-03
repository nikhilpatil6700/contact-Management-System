package com.smartcontact.deo;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.entity.User;
import com.smartcontact.entity.contact;



public interface ContactRepository extends JpaRepository<contact, Integer>{
	
	 //current Page No
	 //Contact per page
	//we can give informaion about pages with the help of Pageable interface 
	
	@Query("from contact as c where c.us.id=:userid")
	public Page<contact> getByUserId(@Param("userid") int userid,Pageable pageble);
	
	@Modifying
	@Query("delete from contact c where c.cid = :cid")
	void deleteById(int cid);
	
	
	public List<contact> findByNameContainingAndUs(String name,User us);

}
