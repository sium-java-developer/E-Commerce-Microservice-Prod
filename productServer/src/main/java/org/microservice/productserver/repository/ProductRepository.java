package org.microservice.productserver.repository;

import java.util.List;

import org.microservice.productserver.model.ProductOR;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "productdata", path = "productdata")
public interface ProductRepository extends CrudRepository<ProductOR, Long> {

    public List<ProductOR> findByCode(@Param("code") String  code);
}