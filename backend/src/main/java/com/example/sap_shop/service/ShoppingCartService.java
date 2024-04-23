package com.example.sap_shop.service;

import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.ShoppingCartDTO;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.error.NotEnoughQuantityException;
import com.example.sap_shop.error.TokenExpiredException;
import com.example.sap_shop.model.JwtUtil;
import com.example.sap_shop.model.OrderItem;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.model.ShoppingCart;
import com.example.sap_shop.repository.OrderItemRepository;
import com.example.sap_shop.repository.ProductRepository;
import com.example.sap_shop.repository.ShoppingCartRepository;
import com.example.sap_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, UserRepository userRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, JwtUtil jwtUtil) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.jwtUtil = jwtUtil;
    }

    public void checkTokenDate(String token) throws TokenExpiredException {
        if(jwtUtil.extractExpiration(token.substring(7)).before(new Date())){
            throw new TokenExpiredException("Your session had ended, please login again");
        }
    }

    private void checkOrderItemNotNullNotEmpty(OrderItemDTO orderItemDTO) throws InvalidRequestBodyException {
        if(orderItemDTO.getQuantity() <= 0){
            throw new InvalidRequestBodyException("You can't have quantity under 0!");
        }
        if(orderItemDTO.getProduct() == null){
            throw new InvalidRequestBodyException("You don't have product to add!");
        }
        if(orderItemDTO.getProduct().getName() == null || orderItemDTO.getProduct().getName().equals("")){
            throw new InvalidRequestBodyException("There is not product name to add!");
        }
    }

    public ShoppingCartDTO getShoppingCart(String token) throws TokenExpiredException {
        checkTokenDate(token);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token.substring(7))));
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        for(OrderItem orderItem : shoppingCart.getOrderItems()){
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(orderItem.getProduct().getName());
            productDTO.setQuantity(orderItem.getProduct().getQuantity());
            productDTO.setImagePath(orderItem.getProduct().getImagePath());
            productDTO.setPrice(orderItem.getProduct().getPrice());
            productDTO.setDiscountPrice(orderItem.getProduct().getDiscountPrice());
            productDTO.setDescription(orderItem.getProduct().getDescription());
            orderItemDTO.setId(orderItem.getId());
            orderItemDTO.setQuantity(orderItem.getQuantity());
            orderItemDTO.setProduct(productDTO);
            orderItemDTOS.add(orderItemDTO);
        }
        shoppingCartDTO.setOrderItemDTOS(orderItemDTOS);
        return shoppingCartDTO;
    }

    @Transactional
    public void addProductToShoppingCart(String token, OrderItemDTO orderItemDTO) throws TokenExpiredException, NotEnoughQuantityException, InvalidRequestBodyException {
        checkTokenDate(token);
        if(orderItemDTO.getProduct() == null){
            throw new InvalidRequestBodyException("You don't have product to add!");
        }
        if(orderItemDTO.getProduct().getName() == null || orderItemDTO.getProduct().getName().equals("")){
            throw new InvalidRequestBodyException("There is not product name to add!");
        }
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token.substring(7))));
        List<OrderItem> orderItems = shoppingCart.getOrderItems();
        for(OrderItem orderItem : orderItems){
            if(orderItem.getProduct().getName().equals(orderItemDTO.getProduct().getName())){
                throw new InvalidRequestBodyException("Product with name " + orderItemDTO.getProduct().getName() + "is already in your cart!");
            }
        }
        Product product = productRepository.findByName(orderItemDTO.getProduct().getName());
        if(product.getQuantity() < 1){
            throw new NotEnoughQuantityException("Sorry, but for this moment we do not have this quantity of the product.");
        }
        if(orderItemDTO.getProduct().getName() == null || orderItemDTO.getProduct().getName().equals("")){
            throw new InvalidRequestBodyException("There is not product name to add!");
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItem = orderItemRepository.save(orderItem);
        orderItems.add(orderItem);
        shoppingCart.setOrderItems(orderItems);
        shoppingCartRepository.save(shoppingCart);
    }

    @Transactional
    public void removeProductFromShoppingCart(String token, String id) throws TokenExpiredException, InvalidRequestBodyException {
        checkTokenDate(token);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token.substring(7))));
        List<OrderItem> orderItems = new ArrayList<>(shoppingCart.getOrderItems());
        Optional<OrderItem> orderItemToDelete = orderItemRepository.findById(Long.parseLong(id));
        orderItems.remove(orderItemToDelete.get());
        shoppingCart.setOrderItems(orderItems);
        shoppingCartRepository.save(shoppingCart);
        orderItemRepository.delete(orderItemToDelete.get());
    }

    @Transactional
    public void changeProductQuantityToShoppingCart(String token, OrderItemDTO orderItemDTO) throws TokenExpiredException, NotEnoughQuantityException, InvalidRequestBodyException {
        checkTokenDate(token);
        checkOrderItemNotNullNotEmpty(orderItemDTO);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token.substring(7))));
        List<OrderItem> orderItems = shoppingCart.getOrderItems();
        Product product = productRepository.findByName(orderItemDTO.getProduct().getName());
        if (product.getQuantity() < orderItemDTO.getQuantity()){
            throw new NotEnoughQuantityException("Sorry, but for this moment we do not have this quantity of the product.");
        }
        Optional<OrderItem> orderItemToChange = orderItemRepository.findById(orderItemDTO.getId());
        orderItemToChange.get().setQuantity(orderItemDTO.getQuantity());
        orderItemRepository.save(orderItemToChange.get());
        orderItems.get(orderItems.indexOf(orderItemToChange.get())).setQuantity(orderItemDTO.getQuantity());
        shoppingCart.setOrderItems(orderItems);
        shoppingCartRepository.save(shoppingCart);
    }
}

