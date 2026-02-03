package org.microservice.productserver.model;


import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name ="product")
public class ProductOR{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "productid")
    private Long productId;

    @Column(name = "prodname")
    private String name;

    @Column(name = "code")
    private String code;;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private Double price;

}