package com.example.sap_shop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.sap_shop.dto.UserDto;
import com.example.sap_shop.error.*;
import com.example.sap_shop.model.*;
import com.example.sap_shop.repository.ShoppingCartRepository;
import com.example.sap_shop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ShoppingCartRepository shoppingCartRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User createMockUser(){
        User user = new User();
        user.setUsername("User");
        user.setPassword("Password"); // Assuming the password encoder simply returns "encodedPassword" for simplicity
        user.setEmail("user@abv.bg");
        ShoppingCart shoppingCart = new ShoppingCart();
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(2);
        Product product = new Product();
        product.setDiscountPrice(20f);
        product.setPrice(20f);
        product.setDescription("product description");
        product.setQuantity(20);
        product.setMinPrice(10f);
        product.setName("qdki");
        Category category = new Category();
        category.setName("sheikove");
        product.setCategory(category);

        orderItem.setProduct(product);
        orderItems.add(orderItem);
        shoppingCart.setOrderItems(orderItems);

        user.setShoppingCart(shoppingCart);
        Role defaultRole = new Role();
        defaultRole.setId(2);
        List<Role> roles = new ArrayList<>();
        roles.add(defaultRole);
        user.setRoles(roles);

        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setStatus("In progress");
        order.setTotalPrice(40f);
        order.setOrderItems(orderItems);
        orders.add(order);
        user.setOrders(orders);
        return  user;
    }

    @Test
    void registerUserWithEmptyFieldsThrowsException() {
        UserDto newUser = new UserDto("", "", ""); // Empty fields
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        assertThrows(EmptyCredentialException.class, () -> {
            userService.registerNewUser(newUser);
        });
    }

    @Test
    public void testRegisterNewUser_Success() throws Exception {
        UserDto newUserDto = new UserDto("newUser", "password123", "newuser@example.com");
        User savedUser = new User();
        savedUser.setUsername(newUserDto.getUsername());
        savedUser.setPassword("encodedPassword"); // Assuming the password encoder simply returns "encodedPassword" for simplicity
        savedUser.setEmail(newUserDto.getEmail());
        ShoppingCart newShoppingCart = new ShoppingCart();
        Role defaultRole = new Role();
        defaultRole.setId(2);
        List<Role> roles = new ArrayList<>();
        roles.add(defaultRole);
        savedUser.setRoles(roles);

        when(userRepository.findByUsername(newUserDto.getUsername())).thenReturn(null);
        when(userRepository.findByEmail(newUserDto.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(newUserDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(newShoppingCart);

        userService.registerNewUser(newUserDto);

        verify(userRepository, times(2)).save(any(User.class));
        verify(shoppingCartRepository, times(2)).save(any(ShoppingCart.class));

        assertNotNull(savedUser.getShoppingCart());
        assertEquals(savedUser.getUsername(), newUserDto.getUsername());
        assertEquals(savedUser.getPassword(), "encodedPassword");
        assertEquals(newShoppingCart.getUser(), savedUser);
        assertNotNull(savedUser.getRoles());
        assertFalse(savedUser.getRoles().isEmpty());
        assertEquals(2, savedUser.getRoles().get(0).getId().intValue());
    }

    @Test
    void registerUserWithExistingEmailThrowsException() {
        UserDto newUser = new UserDto("username", "password", "existingEmail@example.com");
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(new User());

        Exception exception = assertThrows(UserAlreadyExistException.class, () -> {
            userService.registerNewUser(newUser);
        });

        assertEquals(exception.getMessage(), "User with that email already exists");
    }

    @Test
    void registerUserWithExistingUsernameThrowsException() {
        UserDto newUser = new UserDto("Existing username", "password", "email@example.com");
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(new User());

        Exception exception = assertThrows(UserAlreadyExistException.class, () -> {
            userService.registerNewUser(newUser);

        });

        assertEquals(exception.getMessage(), "User with that username already exists");
    }

    @Test
    void loginUserWithNonExistentUsernameThrowsException() {
        UserDto userDto = new UserDto("nonexistentUser", "password", "email@example.com");
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(null);

        Exception exception = assertThrows(InvalidLoginCredentialException.class, () -> {
            userService.loginUser(userDto);
        });

        assertEquals(exception.getMessage(), "User with this username is not found");
    }

    @Test
    void loginUserWithIncorrectPasswordThrowsException() {
        UserDto userDto = new UserDto("existingUser", "wrongPassword", "email@example.com");
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword("encodedCorrectPassword");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(userDto.getPassword(), user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(InvalidLoginCredentialException.class, () -> {
            userService.loginUser(userDto);
        });

        String expectedMessage = "Password for this user is incorrect";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void loginUserWithCorrectCredentialsReturnsJwtToken() {
        UserDto userDto = new UserDto("existingUser", "correctPassword", "email@example.com");
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword("encodedCorrectPassword");
        String expectedToken = "jwtToken";

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(userDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(userDto.getUsername())).thenReturn(expectedToken);

        String actualToken = null;
        try {
            actualToken = userService.loginUser(userDto);
        } catch (InvalidLoginCredentialException e) {
            e.printStackTrace();
        }

        assertEquals(expectedToken, actualToken);
    }

    @Test
    void getUserRolesReturnsListOfRoles() {
        String username = "existingUser";
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        User user = new User();
        Role roleAdmin = new Role();
        roleAdmin.setRole("ADMIN");
        Role roleUser = new Role();
        roleUser.setRole("USER");
        user.setRoles(Arrays.asList(roleAdmin, roleUser));

        when(userRepository.findByUsername(username)).thenReturn(user);

        List<String> roles = null;
        try {
            roles = userService.getUserRoles(userDto);
        } catch (InvalidLoginCredentialException e) {
            e.printStackTrace();
        }

        assertNotNull(roles);
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("USER"));
    }

    @Test
    void getUserRolesThrowsExceptionWhenUserNotFound() {
        UserDto userDto = new UserDto();
        userDto.setUsername("nonExistingUser");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(null);

        Exception exception = assertThrows(InvalidLoginCredentialException.class, () -> {
            userService.getUserRoles(userDto);
        });

        String expectedMessage = "User with this username is not found";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void getProfileInfoWithValidTokenReturnsUserDto() {
        String token = "Bearer validToken";
        User user = createMockUser();
        Date date = new Date(System.currentTimeMillis()+200);
        when(jwtUtil.extractExpiration(anyString())).thenReturn(date);
        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        UserDto userDto = null;
        try {
            userDto = userService.getProfileInfo(token);
        } catch (TokenExpiredException e) {
            e.printStackTrace();
        }

        assertNotNull(userDto);
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertNotNull(user.getShoppingCart());
        assertNotNull(user.getOrders());
        assertEquals(user.getOrders().get(0).getTotalPrice(), 40f);
        assertEquals(user.getShoppingCart().getOrderItems().get(0).getQuantity(), 2);
    }

    @Test
    void getProfileInfoWithExpiredTokenThrowsException() {
        String token = "Bearer expiredToken";
        when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() - 200));

        Exception exception = assertThrows(TokenExpiredException.class, () -> {
            userService.getProfileInfo(token);
        });

        assertEquals(exception.getMessage(), "Your session had ended, please login again");
    }

    @Test
    void updateUserWithValidTokenUpdatesUserInfo() {
        String token = "Bearer validToken";
        UserDto userDto = new UserDto("updatedUsername", "updatedPassword", "updatedEmail@example.com");
        User user = new User();
        user.setUsername("originalUsername");
        user.setEmail("originalEmail@example.com");

        when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 200));
        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        try {
            userService.updateUser(token, userDto);
        } catch (TokenExpiredException | UserAlreadyExistException e) {
            e.printStackTrace();
        }

        verify(userRepository, times(1)).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals(userDto.getUsername(), updatedUser.getUsername());
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUserRoleUpdatesRolesCorrectly() {
        String username = "existingUser";
        List<String> newRoles = List.of("ADMIN", "USER");
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        userService.updateUserRole(username, newRoles);

        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertNotNull(updatedUser.getRoles());
        assertEquals(2, updatedUser.getRoles().size());
        assertTrue(updatedUser.getRoles().stream().anyMatch(role -> role.getId() == 1));
        assertTrue(updatedUser.getRoles().stream().anyMatch(role -> role.getId() == 2));
    }

    @Test
    void deleteUserWhenUserExists() {
        String username = "existingUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        try {
            userService.deleteUser(username);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserThrowsExceptionWhenUserNotFound() {
        String username = "nonExistingUser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(username);
        });

        assertEquals(exception.getMessage(), "User is not found");
    }
}