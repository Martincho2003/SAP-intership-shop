package com.example.sap_shop.service;

import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.error.ProductAlreadyExistException;
import com.example.sap_shop.error.ProductNotFoundException;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void createProduct(ProductDTO productDTO) throws ProductAlreadyExistException, FieldCannotBeEmptyException {
        if(productRepository.findByName(productDTO.getName()) != null){
            throw new ProductAlreadyExistException("Product already exist");
        }

        Product product = new Product();
        if (productDTO.getName() != null && productDTO.getDescription() != null && productDTO.getPrice() != null && productDTO.getMinPrice() != null && productDTO.getQuantity() != null && productDTO.getQuantity() != null && productDTO.getImagePath() != null) {
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setMinPrice(productDTO.getMinPrice());
            product.setQuantity(productDTO.getQuantity());
            product.setImagePath(productDTO.getImagePath());
            productRepository.save(product);
        } else {
            throw new FieldCannotBeEmptyException("Field can not be empty!");
        }

    }

    @Transactional
    public void deleteProduct(String name) throws ProductNotFoundException {

        if (productRepository.findByName(name) == null) {
            throw new ProductNotFoundException("Product not found with name: " + name); // opravi greshkata negramotnik
        }
        productRepository.deleteByName(name);

    }

    public List<ProductDTO> findAllProducts() {
            return productRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

    }

    public List<ProductDTO> findByNameContainingIgnoreCase(String name) {

        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getImagePath(), product.getMinPrice());
    }
}
