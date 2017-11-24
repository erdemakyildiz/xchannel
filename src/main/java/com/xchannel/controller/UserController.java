package com.xchannel.controller;

import com.xchannel.entity.FirebaseToken;
import com.xchannel.entity.User;
import com.xchannel.service.FirebaseTokenRegisterService;
import com.xchannel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by erdem akyildiz on 22.11.2017.
 */
@Controller
@RequestMapping(path = "user")
public class UserController {

    @Autowired
    private FirebaseTokenRegisterService firebaseTokenRegisterService;

    @Autowired
    UserService userService;

    @GetMapping(path = "login")
    public ModelAndView login(){
        return new ModelAndView("login");
    }

    @PostMapping(path = "token")
    @ResponseBody
    public void registerToken(@RequestParam("token") String token){
        if (firebaseTokenRegisterService.findToken(token) == null) {
            FirebaseToken firebaseToken = new FirebaseToken();
            firebaseToken.setFirebaseToken(token);

            firebaseTokenRegisterService.saveToken(firebaseToken);
        }
    }

    @PostMapping(path = "save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUser(User user){
        if (StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getUsername()) ||
                StringUtils.isEmpty(user.getPassword())){

            return ResponseEntity.badRequest().build();
        }

        User savedUser = userService.saveUser(user);

        if (!StringUtils.isEmpty(savedUser.getId()))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping(path = "get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<User> getUser(HttpServletRequest httpRequest){
        User user = userService.getUser(httpRequest.getRemoteUser());

        if (user == null){
            return ResponseEntity.badRequest().body(null);
        }else{
            user.setPassword("");
            return ResponseEntity.ok().body(user);
        }

    }

    @RequestMapping(path = "/success")
    public ResponseEntity loginSuccess(){
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/error")
    public ResponseEntity loginError(){
        return ResponseEntity.badRequest().build();
    }

}
