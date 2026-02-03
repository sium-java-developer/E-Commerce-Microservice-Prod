package org.microservice.productserver.model;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product{

    //@ApiModelProperty(position = 1)
    private String productId;

    //@ApiModelProperty(position = 2)
    private String name;

    //@ApiModelProperty(position = 3)
    private String code;;

    //@ApiModelProperty(position = 4)
    private String title;

    //@ApiModelProperty(position = 5)
    private Double price;
}