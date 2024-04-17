package com.example.sap_shop.service;

import com.example.sap_shop.dto.DiscountDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.model.Discount;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.repository.DiscountRepository;
import com.example.sap_shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void createDiscount(DiscountDTO discountDTO){
        Discount discount = new Discount();
        List<Product> products = new ArrayList<>();
        for(ProductDTO productDTO : discountDTO.getProductDTOS()) {
            Product product = productRepository.findByName(productDTO.getName());
            products.add(product);
        }
        discount.setProducts(products);
        discount.setName(discountDTO.getName());
        discount.setEndDate(discountDTO.getEndDate());
        discount.setStartDate(discountDTO.getStartDate());
        discount.setPercentage(discountDTO.getPercentage());
        discountRepository.save(discount);
    }

    @Transactional
    public void updateDiscountSettings(DiscountDTO discountDTO){ // TODO: Fix update to not update everything
        Discount discount = discountRepository.findByName(discountDTO.getName());
        if (discountDTO.getName() != null && discountDTO.getName() != "") {
            discount.setName(discountDTO.getName());
        }
        if(discountDTO.getEndDate() != null) {
            discount.setEndDate(discountDTO.getEndDate());
        }
        if (discountDTO.getStartDate() != null) {
            discount.setStartDate(discountDTO.getStartDate());
        }
        if (discountDTO.getPercentage() != null) {
            discount.setPercentage(discountDTO.getPercentage());
        }
        discountRepository.save(discount);
    }

    @Transactional
    public void updateDiscountProducts(DiscountDTO discountDTO){ // TODO: Fix update to not update everything
        Discount discount = discountRepository.findByName(discountDTO.getName());
        List<Product> products = new ArrayList<>();
        for(ProductDTO productDTO : discountDTO.getProductDTOS()) {
            Product product = productRepository.findByName(productDTO.getName());
            products.add(product);
        }
        discount.setProducts(products);
        discountRepository.save(discount);
    }

    @Transactional
    public void deleteDiscount(String discountName){
        discountRepository.delete(discountRepository.findByName(discountName));
    }

    public List<DiscountDTO> getDiscountsByName(String discountName){
        List<Discount> discounts = discountRepository.findByNameContainingIgnoreCase(discountName);
        List<DiscountDTO> discountDTOS = new ArrayList<>();
        for (Discount discount : discounts){
            DiscountDTO discountDTO = new DiscountDTO();
            discountDTO.setName(discount.getName());
            discountDTO.setEndDate(discount.getEndDate());
            discountDTO.setPercentage(discount.getPercentage());
            discountDTO.setStartDate(discount.getStartDate());
            List<ProductDTO> productDTOS = new ArrayList<>();
            for (Product product : discount.getProducts()){
                productDTOS.add(new ProductDTO(product.getName(), product.getDescription(), product.getDiscountPrice(), product.getQuantity(), product.getImagePath(), product.getMinPrice()));
            }
            discountDTO.setProductDTOS(productDTOS);
            discountDTOS.add(discountDTO);
        }
        return discountDTOS;
    }

    // TODO: Add checks and exceptions

    @Transactional
    @Scheduled(cron = "23 59 50 * * *")
    public void updatePriceOnExpiredDiscounts(){
        List<Discount> discounts = discountRepository.findByEndDate(new Date());
        for(Discount discount : discounts){
            for(Product product : discount.getProducts()){
                product.setDiscountPrice(product.getPrice());
                productRepository.save(product);
            }
            discountRepository.delete(discount);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePriceOnActivatedDiscounts(){
        List<Discount> discounts = discountRepository.findByStartDate(new Date());
        for(Discount discount : discounts){
            Integer percentage = discount.getPercentage();
            for(Product product : discount.getProducts()){
                Float price = product.getPrice();
                if (price - price * (percentage / 100.0f) >= product.getMinPrice()) {
                    product.setDiscountPrice(price - price * (percentage / 100.0f));
                } else {
                    product.setDiscountPrice(product.getMinPrice());
                }
            }
        }
    }
}
