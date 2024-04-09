package com.example.sap_shop.service;

import com.example.sap_shop.dto.UserDto;
import com.example.sap_shop.error.InvalidLoginCredentialException;
import com.example.sap_shop.error.UserAlreadyExistException;
import com.example.sap_shop.model.JwtUtil;
import com.example.sap_shop.model.Role;
import com.example.sap_shop.model.User;
import com.example.sap_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void registerNewUser(UserDto userDto) throws UserAlreadyExistException {
        if(userRepository.findByEmail(userDto.getEmail()) != null){
            throw new UserAlreadyExistException("User with that email already exists");
        }
        if(userRepository.findByUsername(userDto.getUsername()) != null){
            throw new UserAlreadyExistException("User with that username already exists");
        }
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        Role role = new Role();
        role.setId(2);
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    public String loginUser(UserDto userDto) throws InvalidLoginCredentialException {
        User user;
        if ((user = userRepository.findByUsername(userDto.getUsername())) == null) {
            throw new InvalidLoginCredentialException("User with this username is not found");
        }
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new InvalidLoginCredentialException("Password for this user is incorrect");
        }
        return jwtUtil.generateToken(userDto.getUsername());
    }
}
