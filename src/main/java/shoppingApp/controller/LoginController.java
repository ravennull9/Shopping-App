package shoppingApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import shoppingApp.domain.User;
import shoppingApp.domain.request.LoginRequestBody;
import shoppingApp.domain.response.LoginResponseBody;
import shoppingApp.secutiry.AuthUserDetail;
import shoppingApp.secutiry.JwtProvider;

@RestController
public class LoginController {
    private AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    private JwtProvider jwtProvider;

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponseBody postLogin(@RequestBody LoginRequestBody loginRequestBody){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestBody.getUsername(), loginRequestBody.getPassword())
            );
        } catch (AuthenticationException e){
            throw new BadCredentialsException("Provided credential is invalid.");
        }

        //Successfully authenticated user will be stored in the authUserDetail object
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal(); //getPrincipal() returns the user object

        //A token wil be created using the username/email/userId and permission
        String token = jwtProvider.createToken(authUserDetail);

        //Returns the token as a response to the frontend/postman
        return LoginResponseBody.builder()
                .message("Welcome " + authUserDetail.getUsername())
                .token(token)
                .build();
    }
}
