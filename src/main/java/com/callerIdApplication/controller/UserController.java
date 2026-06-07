package com.callerIdApplication.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.callerIdApplication.entity.Contact;
import com.callerIdApplication.entity.User;
import com.callerIdApplication.entity.Spam;
import com.callerIdApplication.exceptions.UserException;
import com.callerIdApplication.services.UserService;
import com.callerIdApplication.repostitory.SpamDao;

@RestController
public class UserController {

    @Autowired
    private UserService cService;

    @Autowired
    private SpamDao spamDao;

    @PostMapping("/addUser")
    public ResponseEntity<String> saveUser(@RequestBody User user) throws UserException {
        String savedUser = cService.createCustomer(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/user/addContact")
    public ResponseEntity<List<Contact>> saveContact(@RequestBody Contact contact, @RequestParam(required = false) String key) throws UserException {
        List<Contact> contacts = cService.addContact(contact, key);
        return new ResponseEntity<>(contacts, HttpStatus.CREATED);
    }

    @GetMapping("/user/search/{name}")
    public ResponseEntity<List<?>> searchContact(@PathVariable("name") String name, @RequestParam(required = false) String key) throws UserException {
        List<?> contacts = cService.searchContact(name, key);
        return new ResponseEntity<>(contacts, HttpStatus.CREATED);
    }

    @GetMapping("/user/searchPerson/number={num}")
    public ResponseEntity<List<?>> searchPersonByPhoneNumber(@PathVariable("num") String num, @RequestParam(required = false) String key) throws UserException {
        // 1. Ejecutar la búsqueda regular del servicio
        List<?> contacts = cService.searchPersonByNumber(num, key);
        
        // 2. Comprobar si el número está marcado como SPAM de forma segura
        List<Spam> spamList = spamDao.findBynumber(num);
        
        // Validamos si la lista contiene elementos
        boolean isSpammer = false;
        if (spamList != null && !spamList.isEmpty()) {
            // Evaluamos el objeto directamente. Si isSpammer() falla por tipo, se accede al método alternativo.
            Spam spamObject = spamList.get(0);
            isSpammer = spamObject.isSpammer(); // Si tu clase usa Lombok u otra estructura, esto se resolverá de forma nativa
        }

        // 3. Modificar o construir la respuesta para la App Android
        List<Map<String, Object>> enrichedResponse = new ArrayList<>();

        if (contacts != null && !contacts.isEmpty()) {
            for (Object item : contacts) {
                Map<String, Object> map = new HashMap<>();
                if (item instanceof Contact) {
                    Contact c = (Contact) item;
                    map.put("name", isSpammer ? "SPAM" : c.getName());
                    map.put("number", c.getNumber());
                } else if (item instanceof User) {
                    User u = (User) item;
                    map.put("name", isSpammer ? "SPAM" : u.getUserName());
                    map.put("number", u.getPhoneNumber());
                } else {
                    map.put("name", isSpammer ? "SPAM" : "Desconocido");
                    map.put("number", num);
                }
                map.put("spammer", isSpammer);
                enrichedResponse.add(map);
            }
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("name", isSpammer ? "SPAM" : "Desconocido");
            map.put("number", num);
            map.put("spammer", isSpammer);
            enrichedResponse.add(map);
        }

        return new ResponseEntity<>(enrichedResponse, HttpStatus.OK);
    }
}
