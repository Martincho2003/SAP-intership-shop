package com.example.sap_shop.service;

import com.example.sap_shop.dto.DiscountDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.DiscountNotFoundException;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.error.ProductNotFoundException;
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
    public void createDiscount(DiscountDTO discountDTO) throws InvalidRequestBodyException, ProductNotFoundException {
        if(discountDTO.getProductDTOS() == null || discountDTO.getPercentage() == null || discountDTO.getName() == null
                || discountDTO.getStartDate() == null || discountDTO.getEndDate() == null){
            throw new InvalidRequestBodyException("Some of the settings are not set");
        }
        if(discountDTO.getPercentage() <= 0 || discountDTO.getName() == ""
                || discountDTO.getStartDate().before(new Date()) || discountDTO.getEndDate().before(discountDTO.getStartDate())){
            throw new InvalidRequestBodyException("Some of the settings are set wrong");
        }
        Discount discount = new Discount();
        List<Product> products = new ArrayList<>();
        for(ProductDTO productDTO : discountDTO.getProductDTOS()) {
            Product product;
            if((product = productRepository.findByName(productDTO.getName())) == null){
                throw new ProductNotFoundException("Product with name " + productDTO.getName() + " not found!");
            }
            products.add(product);
        }
        discount.setProducts(products);
        discount.setName(discountDTO.getName());
        discount.setEndDate(discountDTO.getEndDate());
        discount.setStartDate(discountDTO.getStartDate());
        discount.setPercentage(discountDTO.getPercentage());
        discountRepository.save(discount);
    }

    private Discount checkDiscountNameAndIfExist(String discountName) throws DiscountNotFoundException, InvalidRequestBodyException {
        if (discountName == null || discountName == "") {
            throw new InvalidRequestBodyException("There is no discount name");
        }
        Discount discount;
        if((discount = discountRepository.findByName(discountName)) == null){
            throw new DiscountNotFoundException("Discount with name: "+ discountName + " is not found.");
        }
        return discount;
    }

    @Transactional
    public void updateDiscountSettings(DiscountDTO discountDTO) throws InvalidRequestBodyException, DiscountNotFoundException {
        Discount discount = checkDiscountNameAndIfExist(discountDTO.getName());
        discount.setName(discountDTO.getName());
        if(discountDTO.getEndDate() != null) {
            discount.setEndDate(discountDTO.getEndDate());
        }
        if (discountDTO.getStartDate() != null) {
            discount.setStartDate(discountDTO.getStartDate());
        }
        if (discountDTO.getPercentage() != null) {
            if(discountDTO.getPercentage() <= 0){
                throw new InvalidRequestBodyException("Percentage can't be under 0.");
            }
            discount.setPercentage(discountDTO.getPercentage());
        }
        discountRepository.save(discount);
    }

    @Transactional
    public void updateDiscountProducts(DiscountDTO discountDTO) throws InvalidRequestBodyException, DiscountNotFoundException {
        Discount discount = checkDiscountNameAndIfExist(discountDTO.getName());
        if(discountDTO.getProductDTOS() == null){
            throw new InvalidRequestBodyException("There is no products");
        }
        List<Product> products = new ArrayList<>();
        for(ProductDTO productDTO : discountDTO.getProductDTOS()) {
            Product product = productRepository.findByName(productDTO.getName());
            products.add(product);
        }
        discount.setProducts(products);
        discountRepository.save(discount);
    }

    @Transactional
    public void deleteDiscount(String discountName) throws DiscountNotFoundException, InvalidRequestBodyException {
        Discount discount = checkDiscountNameAndIfExist(discountName);
        discountRepository.delete(discount);
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
                productDTOS.add(new ProductDTO(product.getName(), product.getDescription(), product.getPrice(), product.getDiscountPrice(), product.getQuantity(), product.getImagePath(), product.getMinPrice(), product.getCategory().getName()));
            }
            discountDTO.setProductDTOS(productDTOS);
            discountDTOS.add(discountDTO);
        }
        return discountDTOS;
    }

    @Transactional
    @Scheduled(cron = "23 59 0 * * *")
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
    @Scheduled(cron = "0 1 0 * * *")
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
