package edu.sv.ues.dam235.apirestdemo.implementations;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import edu.sv.ues.dam235.apirestdemo.entities.Product;
import edu.sv.ues.dam235.apirestdemo.repositories.ProductRepository;
import edu.sv.ues.dam235.apirestdemo.services.ProductServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductsImpl implements ProductServices {
    private final ProductRepository productRepository;

    private ProductsImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductsDTO> getAllProducts() {
        List<ProductsDTO> result = new ArrayList<>();
        List<Product> items = this.productRepository.findAll();
        for (Product item : items) {
            result.add(new ProductsDTO(item.getCode(), item.getName(), item.isStatus()));
        }
        return result;
    }

    //CRUD
    @Override
    public ProductsDTO getProductById(Integer code) {
        Optional<Product> item = this.productRepository.findById(code);
        if (item.isEmpty()) {
            return null;
        }
        return new ProductsDTO(item.get().getCode(), item.get().getName(), item.get().isStatus());
    }

    @Override
    public ProductsDTO createProduct(ProductsDTO product) {
        Product item = new Product();
        item.setName(product.getName());
        item.setStatus(product.isStatus());
        Product saved = this.productRepository.save(item);
        return new ProductsDTO(saved.getCode(), saved.getName(), saved.isStatus());
    }

    @Override
    public ProductsDTO updateProduct(Integer code, ProductsDTO product) {
        Optional<Product> found = this.productRepository.findById(code);
        if (found.isEmpty()) {
            return null;
        }
        Product item = found.get();
        item.setName(product.getName());
        item.setStatus(product.isStatus());
        Product saved = this.productRepository.save(item);
        return new ProductsDTO(saved.getCode(), saved.getName(), saved.isStatus());
    }

    @Override
    public boolean deleteProduct(Integer code) {
        if (!this.productRepository.existsById(code)) {
            return false;
        }
        this.productRepository.deleteById(code);
        return true;
    }
}