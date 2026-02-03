package org.microservice.productserver.kafka.client;

import org.microservice.productserver.model.Product;
import org.microservice.productserver.model.ProductOR;
import org.microservice.productserver.model.Products;
import org.microservice.productserver.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProductListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductListener.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @KafkaListener(topics = "${kafka.topic.product.request}", containerFactory = "requestReplyListenerContainerFactory")
    @SendTo
    public Products receive(Products request) {
        LOGGER.info("Received request: {}", request.getOperation());
        
        if (Products.CREATE.equals(request.getOperation())) {
            return createProduct(request);
        } else if (Products.RETRIEVE_ALL.equals(request.getOperation())) {
            return getAllProducts(request);
        } else if (Products.UPDATE.equals(request.getOperation())) {
            return updateProduct(request);
        } else if (Products.DELETE.equals(request.getOperation())) {
            return deleteProduct(request);
        } else if (Products.RETRIEVE_DETAILS.equals(request.getOperation())) {
            return getProduct(request);
        }

        request.setOperation(Products.FAILURE);
        return request;
    }

    private Products createProduct(Products request) {
        LOGGER.info("Creating product...");
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            Product productApi = request.getProducts().get(0);
            LOGGER.info("Product to create: {}", productApi);
            try {
                ProductOR productOR = productMapper.apiToEntity(productApi);
                LOGGER.info("Mapped entity: {}", productOR);
                productOR = productRepository.save(productOR);
                LOGGER.info("Saved entity: {}", productOR);
                
                List<Product> productList = new ArrayList<>();
                productList.add(productMapper.entityToApi(productOR));
                request.setProducts(productList);
                request.setOperation(Products.SUCCESS);
            } catch (Exception e) {
                LOGGER.error("Error creating product", e);
                request.setOperation(Products.FAILURE);
            }
        } else {
            LOGGER.warn("Product creation request is empty");
            request.setOperation(Products.FAILURE);
        }
        return request;
    }

    private Products getAllProducts(Products request) {
        LOGGER.info("Retrieving all products...");
        try {
            Iterable<ProductOR> productORs = productRepository.findAll();
            List<Product> productList = new ArrayList<>();
            productORs.forEach(productOR -> productList.add(productMapper.entityToApi(productOR)));
            LOGGER.info("Found {} products", productList.size());
            
            request.setProducts(productList);
            request.setOperation(Products.SUCCESS);
        } catch (Exception e) {
            LOGGER.error("Error retrieving all products", e);
            request.setOperation(Products.FAILURE);
        }
        return request;
    }

    private Products updateProduct(Products request) {
        LOGGER.info("Updating product...");
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            Product productApi = request.getProducts().get(0);
            LOGGER.info("Product to update: {}", productApi);
            try {
                Optional<ProductOR> optionalProduct = productRepository.findById(Long.parseLong(productApi.getProductId()));
                
                if (optionalProduct.isPresent()) {
                    ProductOR productOR = optionalProduct.get();
                    productOR.setName(productApi.getName());
                    productOR.setCode(productApi.getCode());
                    productOR.setTitle(productApi.getTitle());
                    productOR.setPrice(productApi.getPrice());
                    
                    productOR = productRepository.save(productOR);
                    LOGGER.info("Updated entity: {}", productOR);
                    
                    List<Product> productList = new ArrayList<>();
                    productList.add(productMapper.entityToApi(productOR));
                    request.setProducts(productList);
                    request.setOperation(Products.SUCCESS);
                } else {
                    LOGGER.warn("Product with id {} not found for update", productApi.getProductId());
                    request.setOperation(Products.FAILURE);
                }
            } catch (Exception e) {
                LOGGER.error("Error updating product", e);
                request.setOperation(Products.FAILURE);
            }
        } else {
            LOGGER.warn("Product update request is empty");
            request.setOperation(Products.FAILURE);
        }
        return request;
    }

    private Products deleteProduct(Products request) {
        LOGGER.info("Deleting product...");
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            Product productApi = request.getProducts().get(0);
            LOGGER.info("Product to delete: {}", productApi);
            try {
                productRepository.deleteById(Long.parseLong(productApi.getProductId()));
                LOGGER.info("Deleted product with id: {}", productApi.getProductId());
                request.setOperation(Products.SUCCESS);
            } catch (Exception e) {
                LOGGER.error("Error deleting product", e);
                request.setOperation(Products.FAILURE);
            }
        } else {
            LOGGER.warn("Product delete request is empty");
            request.setOperation(Products.FAILURE);
        }
        return request;
    }

    private Products getProduct(Products request) {
        LOGGER.info("Retrieving product details...");
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            Product productApi = request.getProducts().get(0);
            LOGGER.info("Product to retrieve: {}", productApi);
            try {
                Optional<ProductOR> optionalProduct = productRepository.findById(Long.parseLong(productApi.getProductId()));
                
                if (optionalProduct.isPresent()) {
                    List<Product> productList = new ArrayList<>();
                    productList.add(productMapper.entityToApi(optionalProduct.get()));
                    request.setProducts(productList);
                    request.setOperation(Products.SUCCESS);
                    LOGGER.info("Retrieved product: {}", productList.get(0));
                } else {
                    LOGGER.warn("Product with id {} not found", productApi.getProductId());
                    request.setOperation(Products.FAILURE);
                }
            } catch (Exception e) {
                LOGGER.error("Error retrieving product", e);
                request.setOperation(Products.FAILURE);
            }
        } else {
            LOGGER.warn("Product retrieval request is empty");
            request.setOperation(Products.FAILURE);
        }
        return request;
    }
}
