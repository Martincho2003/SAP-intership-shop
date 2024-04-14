package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.SaleDto;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.model.Discount;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.model.Sale;
import com.example.sap_shop.repository.CategoryRepository;
import com.example.sap_shop.repository.ProductRepository;
import com.example.sap_shop.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository, CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public void createSale(SaleDto saleDto){
        Sale sale = new Sale();
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
        saleRepository.save(sale);
    }

    @Scheduled(cron = "23 59 50 * * *")
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

    @Scheduled(cron = "0 0 0 * * *")
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
