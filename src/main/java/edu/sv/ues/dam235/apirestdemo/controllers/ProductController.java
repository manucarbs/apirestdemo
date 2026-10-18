package edu.sv.ues.dam235.apirestdemo.controllers;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import edu.sv.ues.dam235.apirestdemo.services.ProductServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    final private ProductServices productServices;

    private ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

    @GetMapping
    public ResponseEntity<List<ProductsDTO>> getAllItems() {
        try {
            List<ProductsDTO> items = productServices.getAllProducts();
            if (items.isEmpty()) {
                return ResponseEntity.status(204).build();
            } else {
                return ResponseEntity.ok(items);
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }

    //CRUD
    @GetMapping("/{code}")
    public ResponseEntity<ProductsDTO> getItem(@PathVariable Integer code) {
        try {
            ProductsDTO item = productServices.getProductById(code);
            if (item == null) {
                return ResponseEntity.status(404).build();
            } else {
                return ResponseEntity.ok(item);
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }

    @PostMapping
    public ResponseEntity<ProductsDTO> createItem(@RequestBody ProductsDTO product) {
        try {
            ProductsDTO created = productServices.createProduct(product);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<ProductsDTO> updateItem(@PathVariable Integer code,
                                                  @RequestBody ProductsDTO product) {
        try {
            ProductsDTO updated = productServices.updateProduct(code, product);
            if (updated == null) {
                return ResponseEntity.status(404).build();
            } else {
                return ResponseEntity.ok(updated);
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer code) {
        try {
            if (productServices.deleteProduct(code)) {
                return ResponseEntity.status(204).build();
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }
}