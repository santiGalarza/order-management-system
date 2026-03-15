package com.santiGalarza.order_management.product;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductService {

        /*
    get product with id
    get list of all products
    create a new product POST
    update an existing product PUT
    delete product by id
     */

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDto getProductById(UUID id) {
        Product product = productRepository.findById(id).
                orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toProductResponseDto(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponseDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        Product product = productMapper.productRequestDTOtoProduct(productRequestDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponseDto(savedProduct);
    }

    public ProductResponseDto fullUpdateProduct(UUID id, ProductRequestDto productRequestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.updateProductRequestDto(productRequestDto, product);
        return productMapper.toProductResponseDto(productRepository.save(product));
    }

    public ProductResponseDto partialUpdateProduct(UUID id, ProductPatchRequestDto productPatchRequestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.patchProductFromRequestDto(productPatchRequestDto, product);
        product.setUpdatedAt(LocalDateTime.now());
        return productMapper.toProductResponseDto(productRepository.save(product));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    /*
    product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setSku(productRequestDto.getSku());
        product.setActive(productRequestDto.isActive());
        product.setMinOrderQuantity(productRequestDto.getMinOrderQuantity());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setWeight(productRequestDto.getWeight());
        product.setUpdatedAt(LocalDateTime.now());

     */
}
