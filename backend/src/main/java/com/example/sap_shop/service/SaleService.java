package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.SaleDto;
import com.example.sap_shop.error.*;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.model.Sale;
import com.example.sap_shop.repository.CategoryRepository;
import com.example.sap_shop.repository.ProductRepository;
import com.example.sap_shop.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    private void setSaleFromSaleDto(Sale sale, SaleDto saleDto) throws ProductNotFoundException {
        List<Category> categoryList = new ArrayList<>();
        for(CategoryDTO categoryDTO : saleDto.getCategoryDTOS()){
            Category category = new Category();
            List<Product> products = new ArrayList<>();
            for(ProductDTO productDTO : categoryDTO.getProductDTOS()){
                Product product = productRepository.findByName(productDTO.getName());
                if(product == null){
                    throw new ProductNotFoundException("Product with name " + productDTO.getName() + " not found!");
                }
                products.add(product);
            }
            category.setProducts(products);
            category.setName(categoryDTO.getName());
            categoryList.add(category);
        }
        sale.setCategories(categoryList);
        sale.setPercentage(saleDto.getPercentage());
        sale.setStartDate(saleDto.getStartDate());
        sale.setEndDate(saleDto.getEndDate());
        sale.setName(saleDto.getName());
    }

    @Transactional
    public void createSale(SaleDto saleDto) throws InvalidRequestBodyException, ProductNotFoundException {
        Sale sale = new Sale();
        if(saleDto.getCategoryDTOS() == null || saleDto.getCategoryDTOS().isEmpty()){
            throw new InvalidRequestBodyException("Categories for sale are not set.");
        }
        if(saleDto.getPercentage() == null || saleDto.getPercentage() <= 0){
            throw new InvalidRequestBodyException("Percentage for sale are not set.");
        }
        if(saleDto.getName() == null || saleDto.getName().isEmpty()){
            throw new InvalidRequestBodyException("Name for sale are not set.");
        }
        if(saleDto.getStartDate() == null || saleDto.getEndDate() == null){
            throw new InvalidRequestBodyException("Dates for sale are not set.");
        }
        if(saleDto.getStartDate().before(new Date()) || saleDto.getEndDate().before(saleDto.getStartDate())){
            throw new InvalidRequestBodyException("Dates for sale are not set correctly.");
        }
        setSaleFromSaleDto(sale, saleDto);
        saleRepository.save(sale);
    }

    @Transactional
    public void updateSaleSettings(SaleDto saleDto) throws InvalidRequestBodyException, FieldCannotBeEmptyException {
        Sale sale = saleRepository.findByName(saleDto.getName());
        if (saleDto.getPercentage() != null) {
            if(saleDto.getPercentage() <= 0){
                throw new InvalidRequestBodyException("Sale can't be under 0 percent.");
            }
            sale.setPercentage(saleDto.getPercentage());
        }
        if (saleDto.getStartDate() != null) {
            if(new Date().after(saleDto.getStartDate())){
                throw new InvalidRequestBodyException("The date is already gone,");
            }
            sale.setStartDate(saleDto.getStartDate());
        }
        if (saleDto.getEndDate() != null) {
            sale.setEndDate(saleDto.getEndDate());
        }
        if(saleDto.getName() != null) {
            if(saleDto.getName() == ""){
                throw new FieldCannotBeEmptyException("You must have a name");
            }
            sale.setName(sale.getName());
        }
        saleRepository.save(sale);
    }

    @Transactional
    public void updateSaleCategories(SaleDto saleDto) throws InvalidRequestBodyException, SaleNotFoundException, CategoryNotFoundException {
        if(saleDto.getName() == null){
            throw new InvalidRequestBodyException("There is no sale set!");
        }
        if(saleDto.getCategoryDTOS() == null){
            throw new CategoryNotFoundException("Categories not set");
        }
        Sale sale = saleRepository.findByName(saleDto.getName());
        if(sale == null){
            throw new SaleNotFoundException("Sale with name " + saleDto.getName() + " is not found!");
        }
        List<Category> categoryList = new ArrayList<>();
        for(CategoryDTO categoryDTO : saleDto.getCategoryDTOS()){
            if(categoryDTO.getName() == null){
                throw new InvalidRequestBodyException("There is no category name!");
            }
            Category category = categoryRepository.findByName(categoryDTO.getName());
            if(category == null){
                throw new CategoryNotFoundException("Category with name" + categoryDTO.getName() + " is not found");
            }
            categoryList.add(category);
        }
        sale.setCategories(categoryList);
        saleRepository.save(sale);
    }

    public List<SaleDto> getSalesByName(String saleName){
        List<Sale> sales = saleRepository.findByNameContainingIgnoreCase(saleName);
        List<SaleDto> saleDtos = new ArrayList<>();
        for(Sale sale : sales) {
            SaleDto saleDto = new SaleDto();
            saleDto.setName(sale.getName());
            saleDto.setPercentage(sale.getPercentage());
            saleDto.setStartDate(sale.getStartDate());
            saleDto.setEndDate(sale.getEndDate());
            List<CategoryDTO> categoryDTOS = new ArrayList<>();
            for (Category category : sale.getCategories()) {
                CategoryDTO categoryDTO = new CategoryDTO();
                List<ProductDTO> productDTOS = new ArrayList<>();
                categoryDTO.setName(category.getName());
                for (Product product : category.getProducts()) {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setName(product.getName());
                    productDTO.setDescription(product.getDescription());
                    productDTO.setPrice(product.getPrice());
                    productDTO.setDiscountPrice(product.getDiscountPrice());
                    productDTO.setMinPrice(product.getMinPrice());
                    productDTO.setQuantity(product.getQuantity());
                    productDTO.setImagePath(product.getImagePath());
                    productDTOS.add(productDTO);
                }
                categoryDTO.setProductDTOS(productDTOS);
                categoryDTOS.add(categoryDTO);
            }
            saleDto.setCategoryDTOS(categoryDTOS);
            saleDtos.add(saleDto);
        }
        return saleDtos;
    }

    @Transactional
    public void deleteSale(String saleName) throws SaleNotFoundException {
        Sale sale;
        if((sale = saleRepository.findByName(saleName)) != null) {
            saleRepository.delete(sale);
        } else {
            throw new SaleNotFoundException("Sale for delete is not found.");
        }
    }

    @Transactional
    @Scheduled(cron = "23 59 0 * * *")
    public void updatePriceOnExpiredSales(){
        List<Sale> sales = saleRepository.findByEndDate(new Date());
        for(Sale sale : sales) {
            for (Category category : sale.getCategories()) {
                for (Product product : category.getProducts()) {
                    product.setDiscountPrice(product.getPrice());
                    productRepository.save(product);
                }
                saleRepository.delete(sale);
            }
        }
    }

    @Transactional
    @Scheduled(cron = "0 2 0 * * *")
    public void updatePriceOnActivatedSales(){
        List<Sale> sales = saleRepository.findByStartDate(new Date());
        for(Sale sale : sales){
            for(Category category: sale.getCategories()) {
                Integer percentage = sale.getPercentage();
                for (Product product : category.getProducts()) {
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
}
