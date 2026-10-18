package edu.sv.ues.dam235.apirestdemo.implementations;

import edu.sv.ues.dam235.apirestdemo.configs.CustomerDetailServices;
import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import edu.sv.ues.dam235.apirestdemo.services.AuthServices;
import edu.sv.ues.dam235.apirestdemo.utilities.JwtUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import edu.sv.ues.dam235.apirestdemo.dtos.RegisterDTO;
import edu.sv.ues.dam235.apirestdemo.entities.User;
import edu.sv.ues.dam235.apirestdemo.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Service
public class AuthServicesImpl implements AuthServices {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerDetailServices customerDetailServices;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public TokenDTO login(String user, String pass) {

        TokenDTO token = new TokenDTO();

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user, pass)
            );

            if (authentication.isAuthenticated()) {

                UserDetails usuarioDetail = (UserDetails) authentication.getPrincipal();

                if (customerDetailServices.getUserDetail().getActive()) {
                    token = jwtUtil.generateToken(user, usuarioDetail);
                return token;}
            }

        } catch (BadCredentialsException bad) {

            token.setMsj("Credenciales incorrectas!");
            return null;

        } catch (Exception e) {

            log.error("{}", e);
            token.setMsj("Error innesperado");
            return null;
        }

        return null;
    }

    //Métodos nuevos

    @Override
    public boolean register(RegisterDTO register) {
        if (userRepository.findByEmail(register.getEmail()) != null) {
            return false;
        }
        User user = new User();
        user.setName(register.getName());
        user.setLastName(register.getLastName());
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public void logout(String token) {
        jwtUtil.invalidateToken(token);
    }
}