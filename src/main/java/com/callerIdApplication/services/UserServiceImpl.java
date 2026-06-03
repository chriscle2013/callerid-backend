package com.callerIdApplication.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.callerIdApplication.entity.Contact;
import com.callerIdApplication.entity.CurrentUserSession;
import com.callerIdApplication.entity.Spam;
import com.callerIdApplication.entity.User;
import com.callerIdApplication.exceptions.UserException;
import com.callerIdApplication.repostitory.ContactDao;
import com.callerIdApplication.repostitory.SessionDao;
import com.callerIdApplication.repostitory.SpamDao;
import com.callerIdApplication.repostitory.UserDao;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao cDao;
	
	@Autowired
	private SessionDao sessionDao;
	
	@Autowired
	private ContactDao contactDao;
	
	@Autowired
	private SpamDao spamDao;
	
	
	@Override
	public String createCustomer(User customer)throws UserException {
		
		
		User existingCustomer= cDao.findByphoneNumber(customer.getPhoneNumber());		
		System.out.println(existingCustomer);
		
		if(existingCustomer != null) 
			throw new UserException("Customer Already Registered with Mobile number");
			
		
		cDao.save(customer);
		
			return "User Registeration Success";
			
			
		}




	@Override
	public List<Contact> addContact(Contact contact, String key) throws UserException {
		CurrentUserSession currentUserSession= sessionDao.findByUuid(key);
		if(currentUserSession==null)
			throw new UserException("Login First");
		
		Optional<User> optional =cDao.findById(currentUserSession.getUserId());
		
		User user=optional.get();
		
		user.getContacts().add(contact);
		contact.setUser(user);
		cDao.save(user);
		
		return user.getContacts();
		
	}




	@Override
	public List<?> searchContact(String name, String key) throws UserException {
		CurrentUserSession currentUserSession= sessionDao.findByUuid(key);
		if(currentUserSession==null)
			throw new UserException("Login First");
//		return contactDao.getContactbykeywords(name);
		User user= cDao.findByuserName(name);
		if(user==null)
		{
			List<Contact> contacts= contactDao.getContactbykeywords(name);
			if(contacts.size()==0)
				throw new UserException("Data not found");
			
			List<Spam> spams= spamDao.findByname(name);
			if(spams.size()==0)
			{
				for(Contact c:contacts)
				{
					
							Spam spam=new Spam();
							
							spam.setName(c.getName());
							spam.setNumber(c.getNumber());
							spam.setSpammer(false);
							spams.add(spam);
				}
				return spams;
			}
				
			
			List<Spam> result=new ArrayList<>();
			for(Contact c:contacts)
			{
				for(Spam s:spams)
				{
					if(c.getNumber().equals(s.getNumber()) && c.getName().equals(s.getName()))
					{
						Spam spam=new Spam();
						spam.setSpamID(s.getSpamID());
						spam.setName(c.getName());
						spam.setNumber(c.getNumber());
						spam.setSpammer(true);
						result.add(spam);
					}
					else {
						Spam spam=new Spam();
						spam.setName(c.getName());
						spam.setNumber(c.getNumber());
						spam.setSpammer(false);
						result.add(spam);
					}
				}
			}
			
			
			
			
			return result;
			
		}
		else {
			
			List<Spam> sOptional= spamDao.findByname(user.getUserName());
			
			if(sOptional.size()>0)
			{
				List<Spam> spams=new ArrayList<>();
				spams.add(sOptional.get(0));
				return spams;
			}
			List<Spam> list=new ArrayList<>();
			Spam spam=new Spam();
			spam.setName(user.getUserName());
			spam.setNumber(user.getPhoneNumber());
			spam.setSpammer(false);
			
			list.add(spam);
			return list;
		}
	}




	@Override
	public List<?> searchPersonByNumber(String Number, String key) throws UserException {
		CurrentUserSession currentUserSession = sessionDao.findByUuid(key);
		if (currentUserSession == null)
			throw new UserException("Login First");

		Optional<User> existingUserOptional = cDao.findById(currentUserSession.getUserId());

		User user = cDao.findByphoneNumber(Number);
		
		if (user == null) {
			List<Contact> contacts = contactDao.getAllContactByphoneNumber(Number);
			List<Map<String, Object>> result = new ArrayList<>();
			
			if (contacts.size() == 0) {
				// Buscar si ya existe un reporte en la tabla Spam
				List<Spam> existingSpam = spamDao.findBynumber(Number);
				if (existingSpam != null && !existingSpam.isEmpty()) {
					Map<String, Object> map = new HashMap<>();
					map.put("name", existingSpam.get(0).getName());
					map.put("number", existingSpam.get(0).getNumber());
					map.put("spammer", existingSpam.get(0).getSpammer());
					result.add(map);
					return result;
				}
				
				// Crear nuevo registro (spammer = true)
				Map<String, Object> map = new HashMap<>();
				map.put("name", "Unknown");
				map.put("number", Number);
				map.put("spammer", true);
				result.add(map);
				
				// También guardar en la base de datos
				Spam spam = new Spam();
				spam.setName("Unknown");
				spam.setNumber(Number);
				spam.setSpammer(true);
				spamDao.save(spam);
				
				return result;
			}
			
			List<Spam> spams = spamDao.findBynumber(Number);
			if (spams.size() == 0) {
				for (Contact c : contacts) {
					Map<String, Object> map = new HashMap<>();
					map.put("name", c.getName());
					map.put("number", c.getNumber());
					map.put("spammer", false);
					result.add(map);
				}
				return result;
			}
			
			for (Contact c : contacts) {
				boolean found = false;
				for (Spam s : spams) {
					if (c.getNumber().equals(s.getNumber()) && c.getName().equals(s.getName())) {
						Map<String, Object> map = new HashMap<>();
						map.put("name", c.getName());
						map.put("number", c.getNumber());
						map.put("spammer", true);
						result.add(map);
						found = true;
						break;
					}
				}
				if (!found) {
					Map<String, Object> map = new HashMap<>();
					map.put("name", c.getName());
					map.put("number", c.getNumber());
					map.put("spammer", false);
					result.add(map);
				}
			}
			return result;
			
		} else {
			List<Map<String, Object>> result = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			map.put("name", user.getUserName());
			map.put("number", user.getPhoneNumber());
			
			// Verificar si está en la tabla Spam
			List<Spam> existingSpam = spamDao.findBynumber(Number);
			if (existingSpam != null && !existingSpam.isEmpty()) {
				map.put("spammer", existingSpam.get(0).getSpammer());
			} else {
				map.put("spammer", false);
			}
			result.add(map);
			return result;
		}
	}
}
