package com.callerIdApplication.controller;

import java.util.Map;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.callerIdApplication.entity.LoginDTO;
import com.callerIdApplication.exceptions.LoginException;
import com.callerIdApplication.services.LoginService;


@RestController
public class LoginController {

	@Autowired
	private LoginService customerLogin;
	
	@PostMapping("/login")
public ResponseEntity<?> logInCustomer(@RequestBody LoginDTO dto) throws LoginException {
    
    String uuid = customerLogin.logIntoAccount(dto);
    
    // Devolver un JSON estructurado
    return new ResponseEntity<>(
        Map.of(
            "userId", dto.getPhoneNumber(),
            "uuid", uuid,
            "localDateTime", LocalDateTime.now().toString()
        ),
        HttpStatus.OK
    );
}
	
	@PostMapping("/logout")
	public String logoutCustomer(@RequestParam(required = false) String key) throws LoginException {
		return customerLogin.logOutFromAccount(key);
		
	}
	
	
	
}
