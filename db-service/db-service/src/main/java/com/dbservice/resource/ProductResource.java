package com.dbservice.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dbservice.model.Product;
import com.dbservice.repository.ProductRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;

@RestController
@RequestMapping("/products")
public class ProductResource {
	
	
	private static final Logger log = Logger.getLogger(ProductResource.class.getName());

	@Autowired
	private ProductRepository prrepo;

	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/product")
	ResponseEntity<List<String>> getProducts(){

		log.info("Getting the list of products");
		List<String> productList = prrepo.findAll()
				.stream()
				.map(Product::getName)
				.collect(Collectors.toList());		
		return new ResponseEntity<>(productList,HttpStatus.OK);

	}

	@PostMapping("/product")
	ResponseEntity<Product> addProduct(@RequestBody Product product){
		prrepo.save(product);
		return new ResponseEntity<Product>(HttpStatus.CREATED);
	}

	
	@GetMapping("/product/{name}")
	@HystrixCommand(fallbackMethod= "fallback")
	ResponseEntity<List<String>> getProductByName(@PathVariable("name") String name){

		return new ResponseEntity<>(prrepo.findByName(name)
				.stream()																		
				.collect(Collectors.toList()),HttpStatus.OK);
	}
	
	ResponseEntity<List<String>> fallback(String name) {
		List<String> list = Arrays.asList("Hystrix is working");
		return new ResponseEntity<>(list,HttpStatus.OK);
		
	}
	
	@GetMapping("/product/sleuth")
	public List<String> getSleuthProducts(){
		log.info("sleuth is working");
		return restTemplate.getForObject("http://localhost:8000/products/product", List.class);
	}
}
