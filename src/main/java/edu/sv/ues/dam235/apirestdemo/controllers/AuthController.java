package edu.sv.ues.dam235.apirestdemo.controllers;

import edu.sv.ues.dam235.apirestdemo.dtos.LoginDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import edu.sv.ues.dam235.apirestdemo.services.AuthServices;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sv.ues.dam235.apirestdemo.dtos.RegisterDTO;
import org.springframework.web.bind.annotation.RequestHeader;
import edu.sv.ues.dam235.apirestdemo.utilities.TokenBlacklist;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    final private AuthServices authServices;
    final private TokenBlacklist tokenBlacklist;


    private AuthController(AuthServices authServices, TokenBlacklist tokenBlacklist) {
        this.authServices = authServices;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO authRequest) {

        try {

            System.out.println("DTO enviado: " + authRequest.toString());

            TokenDTO token = authServices.login(
                    authRequest.getUser(),
                    authRequest.getPass()
            );

            if (token == null) {
                return ResponseEntity.status(401).build();
            } else {
                return ResponseEntity.ok(token);
            }

        } catch (Exception e) {
            log.error("{}", e);
        }
            return null;
    }

    //Nuevos métodos
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDTO registerRequest) {
        try {
            if (registerRequest.getEmail() == null || registerRequest.getEmail().isBlank()
                    || registerRequest.getPassword() == null || registerRequest.getPassword().isBlank()) {
                return ResponseEntity.status(400).build();
            }
            if (authServices.register(registerRequest)) {
                return ResponseEntity.status(201).build();
            } else {
                return ResponseEntity.status(409).build();
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        tokenBlacklist.revoke(token);
        return ResponseEntity.ok().build();
    }
}

