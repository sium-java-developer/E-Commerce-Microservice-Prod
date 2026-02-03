package org.microservice.productserver.kafka.client;

import org.mapstruct.Mapper;
import org.microservice.productserver.model.Product;
import org.microservice.productserver.model.ProductOR;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product entityToApi(ProductOR entity);

    ProductOR apiToEntity(Product api);
}