package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.SaleDto;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.model.Sale;
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

    @Autowired
    public SaleService(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    private void setSaleFromSaleDto(Sale sale, SaleDto saleDto){
        List<Category> categoryList = new ArrayList<>();
        for(CategoryDTO categoryDTO : saleDto.getCategoryDTOS()){
            Category category = new Category();
            List<Product> products = new ArrayList<>();
            for(ProductDTO productDTO : categoryDTO.getProductDTOS()){
                Product product = productRepository.findByName(productDTO.getName());
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
        sale.setName(sale.getName());
    }

    @Transactional
    public void createSale(SaleDto saleDto){
        Sale sale = new Sale();
        setSaleFromSaleDto(sale, saleDto);
        saleRepository.save(sale);
    }

    @Transactional
    public void updateSale(SaleDto saleDto){
        Sale sale = saleRepository.findByName(saleDto.getName());
        sale.setPercentage(saleDto.getPercentage());
        sale.setStartDate(saleDto.getStartDate());
        sale.setEndDate(saleDto.getEndDate());
        sale.setName(sale.getName());
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

    // TODO: Add checks and exceptions

    @Transactional
    public void deleteSale(String saleName){
        saleRepository.delete(saleRepository.findByName(saleName));
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
