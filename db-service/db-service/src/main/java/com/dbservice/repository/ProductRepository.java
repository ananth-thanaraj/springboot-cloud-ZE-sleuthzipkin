package com.dbservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dbservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String>{
	
	public List<String> findByName(String name);
 
}
