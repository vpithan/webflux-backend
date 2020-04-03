package com.example.demo.controller;

import java.time.Duration;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@Log
public class ProductController {
  private final ProductRepository repository;

  public ProductController(final ProductRepository repository) {
    this.repository = repository;
  }

  @PostMapping
  public Mono<ResponseEntity<Product>> insert(@RequestBody Product product) {
    log.info(product.toString());
    return this.repository.insert(product).map(p -> new ResponseEntity<>(p, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<Product>> get(@PathVariable final String id) {
    final var product = new Product();
    product.setId(id);
    return this.repository.findOne(Example.of(product))
      .map( p -> new ResponseEntity<>(p, HttpStatus.OK))
      .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Product> list() {
    return this.repository.findAll()
      .delayElements(Duration.ofSeconds(5))
      .map(p -> { log.info(p.toString()); return p; });
  }

}