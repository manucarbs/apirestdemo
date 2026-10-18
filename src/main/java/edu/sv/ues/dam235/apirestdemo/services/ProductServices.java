package edu.sv.ues.dam235.apirestdemo.services;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import java.util.List;

public interface ProductServices {
    public List<ProductsDTO> getAllProducts();

    //CRUD
    public ProductsDTO getProductById(Integer code);
    public ProductsDTO createProduct(ProductsDTO product);
    public ProductsDTO updateProduct(Integer code, ProductsDTO product);
    public boolean deleteProduct(Integer code);
}
