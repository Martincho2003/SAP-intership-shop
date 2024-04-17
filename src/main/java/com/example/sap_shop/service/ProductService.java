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
            product.setDiscountPrice(productDTO.getPrice());
            product.setMinPrice(productDTO.getMinPrice());
            product.setQuantity(productDTO.getQuantity());
            product.setImagePath(productDTO.getImagePath());
            productRepository.save(product);
        } else {
            throw new FieldCannotBeEmptyException("Field can not be empty!");
        }

    }

    @Transactional
    public void deleteProduct(String name) {
        try {
            if (productRepository.findByName(name) == null) {
                throw new ProductNotFoundException("Product not found with name: " + name);
            }
            productRepository.findByName(name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the product: " + e.getMessage(), e);
        }
    }

    public List<ProductDTO> findAllProducts() {
        try {
            return productRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the exception and rethrow as a custom or runtime exception
            throw new RuntimeException("Failed to retrieve all products: " + e.getMessage(), e);
        }
    }

    public List<ProductDTO> findByNameContainingIgnoreCase(String name) {
        try {
            List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
            return products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the exception and rethrow as a custom or runtime exception
            throw new RuntimeException("Failed to retrieve products by name: " + e.getMessage(), e);
        }
    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getImagePath(), product.getMinPrice());
    }
}
