package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.CategoryNotFoundException;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.error.ProductAlreadyExistException;
import com.example.sap_shop.error.ProductNotFoundException;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.repository.CategoryRepository;
import com.example.sap_shop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void createProduct(ProductDTO productDTO) throws ProductAlreadyExistException, FieldCannotBeEmptyException, CategoryNotFoundException {
        if(productRepository.findByName(productDTO.getName()) != null){
            throw new ProductAlreadyExistException("Product already exist.");
        }

        Product product = new Product();

        if (productDTO.getName() != null && productDTO.getDescription() != null && productDTO.getPrice() != null
                && productDTO.getMinPrice() != null && productDTO.getQuantity() != null
                && productDTO.getImagePath() != null && productDTO.getCategoryName() != null) {

            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setDiscountPrice(productDTO.getPrice());
            product.setMinPrice(productDTO.getMinPrice());
            product.setQuantity(productDTO.getQuantity());
            product.setImagePath(productDTO.getImagePath());
            Category category = categoryRepository.findByName(productDTO.getCategoryName());
            if (category == null) {
                throw new CategoryNotFoundException("Category does not exist!");
            }
            product.setCategory(category);

            productRepository.save(product);
        } else {
            throw new FieldCannotBeEmptyException("Field can not be empty!");
        }

    }

    public List<ProductDTO> getAllProductsFromCategory (String categoryName) {
        List<Product> products = productRepository.findByCategoryName(categoryName);
        return products.stream()
                .map(this::convertProductToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCategory(String productName, String categoryName) throws ProductNotFoundException, CategoryNotFoundException {
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new ProductNotFoundException("Product doesn't exist!");
        }

        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new CategoryNotFoundException("Category doesn't exist!");
        }

        product.setCategory(category);
        productRepository.save(product);
    }

    public List<ProductDTO> getAllProducts() {
            return productRepository.findAll().stream()
                    .map(this::convertProductToDTO)
                    .collect(Collectors.toList());
    }

    public List<ProductDTO> findByNameContainingIgnoreCase(String name) {

        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
                .map(this::convertProductToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(String name) throws ProductNotFoundException {

        if (productRepository.findByName(name) == null) {
            throw new ProductNotFoundException("Product with name " + name + " doesn't exist.");
        }
        productRepository.deleteByName(name);

    }

    public ProductDTO convertProductToDTO(Product product) {
        if (product.getDiscountPrice().equals(product.getPrice())) {
            return new ProductDTO(product.getName(), product.getDescription(), product.getPrice(),
                    product.getQuantity(), product.getImagePath(), product.getMinPrice(), product.getCategory().getName() );
        } else {
            return new ProductDTO(product.getName(), product.getDescription(), product.getDiscountPrice(),
                    product.getQuantity(), product.getImagePath(), product.getMinPrice(), product.getCategory().getName());
        }
    }

    public CategoryDTO convertCategoryToDTO(Category category){
        CategoryDTO categoryDTO = new CategoryDTO();
        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product : category.getProducts()){
            ProductDTO productDTO = convertProductToDTO(product);
            productDTOS.add(productDTO);
        }
        categoryDTO.setName(category.getName());
        categoryDTO.setProductDTOS(productDTOS);
        return categoryDTO;
    }
}
