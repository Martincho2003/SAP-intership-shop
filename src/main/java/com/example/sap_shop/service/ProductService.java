package com.example.sap_shop.service;

import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.repository.ProductRepository;
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

    public void createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setImagePath(productDTO.getImagePath());
        productRepository.save(product);
    }

    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> findByNameContainingIgnoreCase(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());

    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(product.getName(), product.getPrice(), product.getQuantity(), product.getImagePath());
    }
}
