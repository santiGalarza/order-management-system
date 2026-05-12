package com.santiGalarza.order_management.product;

import jakarta.validation.Valid;

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
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct
            (@RequestBody @Valid ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> fullUpdateProduct
            (@PathVariable UUID id, @RequestBody @Valid ProductUpdateDto dto) {
        return ResponseEntity.ok(productService.fullUpdateProduct(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> partialUpdateProduct
            (@PathVariable UUID id, @RequestBody @Valid ProductPatchRequestDto productPatchRequestDto) {
        return ResponseEntity.ok(productService.partialUpdateProduct
                (id,productPatchRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
