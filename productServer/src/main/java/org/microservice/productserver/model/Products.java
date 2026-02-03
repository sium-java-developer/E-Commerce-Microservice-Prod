package org.microservice.productserver.model;


import java.util.List;

public class Products {

    public static final String CREATE = "Create";
    public static final String DELETE = "Delete";
    public static final String DELETE_ALL = "Delete_All";
    public static final String UPDATE = "Update";
    public static final String RETRIEVE_ALL = "Retrieve_All";
    public static final String RETRIEVE_DETAILS = "Retrieve_Details";

    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failure";

    private String operation;
    private List<Product> products;

    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}