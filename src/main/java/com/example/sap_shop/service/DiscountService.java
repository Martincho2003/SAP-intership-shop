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

    private void setDiscountFromDiscountDto(Discount discount, DiscountDTO discountDTO){
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
    }

    public void createDiscount(DiscountDTO discountDTO){
        Discount discount = new Discount();
        setDiscountFromDiscountDto(discount, discountDTO);
        discountRepository.save(discount);
    }

    public void updateDiscount(DiscountDTO discountDTO){
        Discount discount = discountRepository.findByName(discountDTO.getName());
        setDiscountFromDiscountDto(discount, discountDTO);
        discountRepository.save(discount);
    }

    public void deleteDiscount(String discountName){
        discountRepository.delete(discountRepository.findByName(discountName));
    }

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
