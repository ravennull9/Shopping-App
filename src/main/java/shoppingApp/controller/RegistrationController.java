package shoppingApp.controller;


import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import shoppingApp.domain.User;
import shoppingApp.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RegistrationController {

    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService){ this.userService = userService; }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestBody @Valid User user, BindingResult bindingResult){
        if(userService.getUserByUsername(user.getUsername()).isPresent()){
            return "username already exists";
        }
        if(userService.getUserByEmail(user.getEmail()).isPresent()){
            return "email already exists";
        }
        if(bindingResult.hasErrors()){
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            errors.forEach(error -> sb.append(error.getDefaultMessage()+"\n"));
            return sb.toString();
        }
        if(user.getUsername().equals("admin")){
            userService.addUser(user, "admin");
        }
        else{
            userService.addUser(user, "user");
        }

        return "Successfully created an account with username=" + user.getUsername();
    }
}
