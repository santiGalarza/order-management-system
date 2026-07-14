package com.santiGalarza.order_management.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse getProduct(UUID id) {
        Product product = productRepository.findById(id).
                orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.createProductRequestToProduct(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse replaceProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.updateProductFromRequest(request, product);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, PatchProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.patchProductFromRequest(request, product);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }

    public Map<UUID, Product> getProductsByIds(List<UUID> ids) {
        List<Product> products = productRepository.findAllById(ids);

        List<UUID> missingIds = ids.stream()
                .filter(id -> products.stream().noneMatch(p -> p.getId().equals(id)))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new ProductNotFoundException("Products not found with ids: " + missingIds);
        }

        return products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    public Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}