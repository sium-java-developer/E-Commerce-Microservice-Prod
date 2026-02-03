package org.microservice.productweb.controller;


import org.microservice.productweb.hateoas.model.Product;
import org.microservice.productweb.model.Products;
import se.callista.blog.synch_kafka.request_reply_util.CompletableFutureReplyingKafkaOperations;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;


@CrossOrigin
@RestController
public class ProductRestController{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);

    @Autowired
    private CompletableFutureReplyingKafkaOperations<String, Products, Products> replyingKafkaTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${kafka.topic.product.request}")
    private String requestTopic;

    @Value("${kafka.topic.product.reply}")
    private String requestReplyTopic;

    //------------------- Retreive all Products --------------------------------------------------------
    @RequestMapping(value = "/productsweb", method = RequestMethod.GET ,produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<ResponseEntity<CollectionModel<Product>>>  getAllProducts(){

        LOGGER.info("Start");
        DeferredResult<ResponseEntity<CollectionModel<Product>>> deferredResult = new DeferredResult<>();

        Products productsRequest = new Products();
        productsRequest.setOperation(Products.RETRIEVE_ALL);

        CompletableFuture<Products> completableFuture =  replyingKafkaTemplate.requestReply(requestTopic, productsRequest);

        completableFuture.thenAccept(products -> {

            List<org.microservice.productweb.model.Product> productList = products.getProducts();

            Link links[] = { linkTo(methodOn(ProductRestController.class).getAllProducts()).withSelfRel(),
                    linkTo(methodOn(ProductRestController.class).getAllProducts()).withRel("getAllProducts") };

            List<Product> list = new ArrayList<Product>();
            if(productList != null) {
                for (org.microservice.productweb.model.Product product : productList) {
                    Product productHateoas = convertEntityToHateoasEntity(product);
                    list.add(productHateoas
                            .add(linkTo(methodOn(ProductRestController.class).getProduct(productHateoas.getProductId()))
                                    .withSelfRel()));
                }
            }
            list.forEach(item -> LOGGER.debug(item.toString()));
            CollectionModel<Product> result = CollectionModel.of(list, links);

            deferredResult.setResult(new ResponseEntity<CollectionModel<Product>>(result, HttpStatus.OK));

        }).exceptionally(ex -> {
            LOGGER.error(ex.getMessage());
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            return null;
        });

        //delay();

        LOGGER.info("Ending");
        return deferredResult;
    }

    private void delay() {

        long secondsToSleep = 6;
        LOGGER.debug(Thread.currentThread().toString());
        LOGGER.debug("Starting to Sleep Seconds : " + secondsToSleep);

        try{
            Thread.sleep(1000 * secondsToSleep);
        }
        catch(Exception e) {
            LOGGER.error("Error : " + e);
        }
        LOGGER.debug("Awakening from Sleep...");

    }

    //------------------- Retreive a Product --------------------------------------------------------
    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<Product>> getProduct(@PathVariable("id") String id) {

        LOGGER.info("Start");
        LOGGER.debug("Fetching Product with id: {}", id);
        LOGGER.info("Thread : " + Thread.currentThread());
        DeferredResult<ResponseEntity<Product>> deferredResult = new DeferredResult<>();

        Products productsRequest = new Products();
        productsRequest.setOperation(Products.RETRIEVE_DETAILS);
        
        org.microservice.productweb.model.Product product = new org.microservice.productweb.model.Product();
        product.setProductId(id);
        List<org.microservice.productweb.model.Product> productRequestList = new ArrayList<>();
        productRequestList.add(product);
        productsRequest.setProducts(productRequestList);

        CompletableFuture<Products> completableFuture =  replyingKafkaTemplate.requestReply(requestTopic, productsRequest);

        completableFuture.thenAccept(products -> {

            List<org.microservice.productweb.model.Product> productList = products.getProducts();
            org.microservice.productweb.model.Product productRetreived = null;
            String productId = null;

            if (productList != null && productList.iterator().hasNext()) {
                productRetreived = productList.iterator().next();
                productId = productRetreived.getProductId();
                LOGGER.debug("Product with productId : {} retreived from Backend Microservice", productId);

                Product productHateoas = convertEntityToHateoasEntity(productRetreived);
                productHateoas.add(linkTo(methodOn(ProductRestController.class).getProduct(productHateoas.getProductId())).withSelfRel());

                deferredResult.setResult(new ResponseEntity<Product>(productHateoas, HttpStatus.OK));

            }
            else {
                LOGGER.debug("Product with productId : {} not retreived from Backend Microservice", id);
                deferredResult.setResult(new ResponseEntity<Product>(HttpStatus.NOT_FOUND));
            }

        }).exceptionally(ex -> {
            LOGGER.error(ex.getMessage());
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            return null;
        });

        LOGGER.info("Ending");
        return deferredResult;
    }


    //------------------- Create a Product --------------------------------------------------------
    @RequestMapping(value = "/productsweb", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<Product>> addProduct(@RequestBody Product product) {

        LOGGER.info("Start");
        LOGGER.debug("Creating Product with code: {}", product.getCode());

        LOGGER.info("Thread : " + Thread.currentThread());
        DeferredResult<ResponseEntity<Product>> deferredResult = new DeferredResult<>();

        Products productsRequest = new Products();
        productsRequest.setOperation(Products.CREATE);
        
        org.microservice.productweb.model.Product productModel = modelMapper.map(product, org.microservice.productweb.model.Product.class);
        List<org.microservice.productweb.model.Product> productRequestList = new ArrayList<>();
        productRequestList.add(productModel);
        productsRequest.setProducts(productRequestList);

        CompletableFuture<Products> completableFuture =  replyingKafkaTemplate.requestReply(requestTopic, productsRequest);

        completableFuture.thenAccept(products -> {

            List<org.microservice.productweb.model.Product> productList = products.getProducts();
            org.microservice.productweb.model.Product productRetreived = null;
            String productId = null;

            if (productList != null && productList.iterator().hasNext()) {
                productRetreived = productList.iterator().next();
                productId = productRetreived.getProductId();
                LOGGER.debug("Product with productId : {} created by Backend Microservice", productId);

                Product productHateoas = convertEntityToHateoasEntity(productRetreived);
                productHateoas.add(linkTo(methodOn(ProductRestController.class).getProduct(productHateoas.getProductId())).withSelfRel());
                deferredResult.setResult(new ResponseEntity<Product>(productHateoas, HttpStatus.OK));

            }
            else {
                LOGGER.debug("Product with code : {} not created by Backend Microservice", product.getCode());
                deferredResult.setResult(new ResponseEntity<Product>(HttpStatus.CONFLICT));
            }

        }).exceptionally(ex -> {
            LOGGER.error(ex.getMessage());
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            return null;
        });

        LOGGER.info("Ending");
        return deferredResult;
    }


    //------------------- Update a Product --------------------------------------------------------
    @RequestMapping(value = "/productsweb/{productId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<Product>> updateProduct(@PathVariable("productId")String id, @RequestBody Product product) {

        LOGGER.info("Start");
        LOGGER.debug("Updating Product with id: {}", id);

        LOGGER.info("Thread : " + Thread.currentThread());
        DeferredResult<ResponseEntity<Product>> deferredResult = new DeferredResult<>();

        Products productsRequest = new Products();
        productsRequest.setOperation(Products.UPDATE);
        
        org.microservice.productweb.model.Product productModel = modelMapper.map(product, org.microservice.productweb.model.Product.class);
        if(productModel.getProductId() == null) productModel.setProductId(id);
        
        List<org.microservice.productweb.model.Product> productRequestList = new ArrayList<>();
        productRequestList.add(productModel);
        productsRequest.setProducts(productRequestList);

        CompletableFuture<Products> completableFuture =  replyingKafkaTemplate.requestReply(requestTopic, productsRequest);

        completableFuture.thenAccept(products -> {

            List<org.microservice.productweb.model.Product> productList = products.getProducts();
            org.microservice.productweb.model.Product productRetreived = null;
            String productId = null;

            if (productList != null && productList.iterator().hasNext()) {
                productRetreived = productList.iterator().next();
                productId = productRetreived.getProductId();
                LOGGER.debug("Product with productId : {} updated by Backend Microservice", productId);

                Product productHateoas = convertEntityToHateoasEntity(productRetreived);
                productHateoas.add(linkTo(methodOn(ProductRestController.class).getProduct(productHateoas.getProductId())).withSelfRel());
                deferredResult.setResult(new ResponseEntity<Product>(productHateoas, HttpStatus.OK));

            }
            else {
                LOGGER.debug("Product with code : {} not updated by Backend Microservice", id);
                deferredResult.setResult(new ResponseEntity<Product>(HttpStatus.NOT_FOUND));
            }


        }).exceptionally(ex -> {
            LOGGER.error(ex.getMessage());
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            return null;
        });
        LOGGER.info("Ending");
        return deferredResult;
    }


    //------------------- Delete a Product --------------------------------------------------------

    @RequestMapping(value = "/productsweb/{productId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<Product>> deleteProduct(@PathVariable("productId")String id) {

        LOGGER.info("Start");
        LOGGER.debug("Deleting Product with id: {}", id);

        LOGGER.info("Thread : " + Thread.currentThread());
        DeferredResult<ResponseEntity<Product>> deferredResult = new DeferredResult<>();

        Products productsRequest = new Products();
        productsRequest.setOperation(Products.DELETE);
        List<org.microservice.productweb.model.Product> productRequestList = new ArrayList<>();
        org.microservice.productweb.model.Product productToDelete = new org.microservice.productweb.model.Product();
        productToDelete.setProductId(id);
        productToDelete.setName("");
        productToDelete.setCode("");
        productToDelete.setTitle("");
        productToDelete.setPrice(0D);
        productRequestList.add(productToDelete);
        productsRequest.setProducts(productRequestList);

        CompletableFuture<Products> completableFuture =  replyingKafkaTemplate.requestReply(requestTopic, productsRequest);

        completableFuture.thenAccept(products -> {

            if (products.getOperation().contentEquals(Products.SUCCESS)) {
                LOGGER.debug("Product with productId : {} deleted by Backend Microservice", id);
                deferredResult.setResult(new ResponseEntity<Product>(HttpStatus.NO_CONTENT));

            }
            else {
                LOGGER.debug("Product with id : {} suspected not deleted by Backend Microservice", id);
                deferredResult.setResult(new ResponseEntity<Product>(HttpStatus.NOT_FOUND));
            }

        }).exceptionally(ex -> {
            LOGGER.error(ex.getMessage());
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            return null;
        });
        LOGGER.info("Ending");
        return deferredResult;
    }

    private Product convertEntityToHateoasEntity(org.microservice.productweb.model.Product product){
        return  modelMapper.map(product,  Product.class);
    }

}