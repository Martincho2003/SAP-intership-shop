package com.example.sap_shop.service;

import com.example.sap_shop.dto.*;
import com.example.sap_shop.error.EmptyCredentialException;
import com.example.sap_shop.error.InvalidLoginCredentialException;
import com.example.sap_shop.error.UserAlreadyExistException;
import com.example.sap_shop.model.*;
import com.example.sap_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public void registerNewUser(UserDto userDto) throws UserAlreadyExistException, EmptyCredentialException {
        if(!checkEmptyFields(userDto)){
            throw new EmptyCredentialException();
        }
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

    public boolean checkEmptyFields(UserDto userDto){
        return !userDto.getUsername().isEmpty() && !userDto.getPassword().isEmpty() && !userDto.getEmail().isEmpty();
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

    public UserDto getProfileInfo(String token){
        User user = userRepository.findByUsername(jwtUtil.extractUsername(token));
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setOrderItemDTOS(OrderItemListToOrderItemDtoList(user.getShoppingCart().getOrderItems()));

        List<OrderDTO> orderDTOS = new ArrayList<>();
        for(Order order : user.getOrders()){
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderDate(order.getOrderDate());
            orderDTO.setStatus(order.getStatus());

            orderDTO.setOrderItems(OrderItemListToOrderItemDtoList(order.getOrderItems()));
            orderDTOS.add(orderDTO);
        }
        return new UserDto(user.getUsername(), user.getEmail(), shoppingCartDTO, orderDTOS);
    }

    private List<OrderItemDTO> OrderItemListToOrderItemDtoList(List<OrderItem> orderItems){
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        for(OrderItem orderItem : orderItems){
            ProductDTO productDTO = new ProductDTO();
            productDTO.setDescription(orderItem.getProduct().getDescription());
            productDTO.setPrice(orderItem.getProduct().getPrice());
            productDTO.setQuantity(orderItem.getProduct().getQuantity());
            productDTO.setName(orderItem.getProduct().getName());

            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setProduct(productDTO);
            orderItemDTO.setQuantity(orderItem.getQuantity());

            orderItemDTOS.add(orderItemDTO);
        }
        return orderItemDTOS;
    }

    public void updateUserRole(UserDto userDto, List<String> roles){
        User user = userRepository.findByUsername(userDto.getUsername());
        List<Role> rolesNew = new ArrayList<>();
        for(String role: roles){
            Role role1 = new Role();
            if(role.equals("ADMIN")){
                role1.setId(1);
                rolesNew.add(role1);
            }
            if(role.equals("USER")){
                role1.setId(2);
                rolesNew.add(role1);
            }
            if(role.equals("WORKER")){
                role1.setId(3);
                rolesNew.add(role1);
            }
        }
        user.setRoles(rolesNew);
    }
}
