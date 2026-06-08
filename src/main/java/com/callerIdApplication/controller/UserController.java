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
@CrossOrigin(origins = "*")
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
        
        // 1. Ejecutar la búsqueda regular en el servicio de contactos existentes
        List<?> contacts = cService.searchPersonByNumber(num, key);
        
        // 2. Consultar la lista histórica de reportes de Spam de la comunidad
        List<Spam> spamList = spamDao.findBynumber(num);
        
        boolean isSpammer = false;
        String communityMostVotedName = null;

        if (spamList != null && !spamList.isEmpty()) {
            // Evaluamos el estado global consultando el primer elemento
            isSpammer = spamList.get(0).getSpammer(); 

            // ALGORITMO TRUECALLER: Contar frecuencias de los nombres sugeridos por la comunidad
            Map<String, Integer> nameFrequencyMap = new HashMap<>();
            for (Spam s : spamList) {
                if (s.getName() != null && !s.getName().trim().isEmpty()) {
                    String candidateName = s.getName().trim();
                    nameFrequencyMap.put(candidateName, nameFrequencyMap.getOrDefault(candidateName, 0) + 1);
                }
            }

            // Seleccionar el nombre que tiene la mayor cantidad de menciones de la comunidad
            int maxVotes = 0;
            for (Map.Entry<String, Integer> entry : nameFrequencyMap.entrySet()) {
                if (entry.getValue() > maxVotes) {
                    maxVotes = entry.getValue();
                    communityMostVotedName = entry.getKey();
                }
            }
        }

        // Definir el nombre definitivo en caso de ser clasificado como Spam
        // Si la comunidad votó un nombre, se usa ese; si no, se usa el comodín "SPAM"
        String finalSpamDisplayName = (communityMostVotedName != null) ? communityMostVotedName : "SPAM";

        // 3. Modificar o construir la respuesta enriquecida para Android
        List<Map<String, Object>> enrichedResponse = new ArrayList<>();

        if (contacts != null && !contacts.isEmpty()) {
            for (Object item : contacts) {
                Map<String, Object> map = new HashMap<>();
                if (item instanceof Contact) {
                    Contact c = (Contact) item;
                    map.put("name", isSpammer ? finalSpamDisplayName : c.getName());
                    map.put("number", c.getNumber());
                } else if (item instanceof User) {
                    User u = (User) item;
                    map.put("name", isSpammer ? finalSpamDisplayName : u.getUserName());
                    map.put("number", u.getPhoneNumber());
                } else {
                    map.put("name", isSpammer ? finalSpamDisplayName : "Desconocido");
                    map.put("number", num);
                }
                map.put("spammer", isSpammer);
                enrichedResponse.add(map);
            }
        } else {
            // Si el número no está en la agenda de nadie pero es un número reportado/desconocido
            Map<String, Object> map = new HashMap<>();
            map.put("name", isSpammer ? finalSpamDisplayName : "Desconocido");
            map.put("number", num);
            map.put("spammer", isSpammer);
            enrichedResponse.add(map);
        }

        return new ResponseEntity<>(enrichedResponse, HttpStatus.OK);
    }
}
