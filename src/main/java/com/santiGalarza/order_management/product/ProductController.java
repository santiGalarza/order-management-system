package com.santiGalarza.order_management.product;

import com.santiGalarza.order_management.product.dto.CreateProductRequest;
import com.santiGalarza.order_management.product.dto.PatchProductRequest;
import com.santiGalarza.order_management.product.dto.ProductResponse;
import com.santiGalarza.order_management.product.dto.UpdateProductRequest;
import jakarta.validation.Valid;

import com.santiGalarza.order_management.security.RequiresPermission;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    @RequiresPermission.ProductRead
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    @RequiresPermission.ProductRead
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @PostMapping
    @RequiresPermission.ProductCreate
    public ResponseEntity<ProductResponse> createProduct
            (@RequestBody @Valid CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @RequiresPermission.ProductUpdate
    public ResponseEntity<ProductResponse> replaceProduct
            (@PathVariable UUID id, @RequestBody @Valid UpdateProductRequest request) {
        return ResponseEntity.ok(productService.replaceProduct(id, request));
    }

    @PatchMapping("/{id}")
    @RequiresPermission.ProductUpdate
    public ResponseEntity<ProductResponse> updateProduct
            (@PathVariable UUID id, @RequestBody @Valid PatchProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id,request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission.ProductDelete
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}