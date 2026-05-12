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

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        Product product = productMapper.productRequestDTOtoProduct(productRequestDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponseDto(savedProduct);
    }

    @Transactional
    public ProductResponseDto fullUpdateProduct(UUID id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.updateProductRequestDto(dto, product);
        return productMapper.toProductResponseDto(productRepository.save(product));
    }

    @Transactional
    public ProductResponseDto partialUpdateProduct(UUID id, ProductPatchRequestDto productPatchRequestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.patchProductFromRequestDto(productPatchRequestDto, product);
        return productMapper.toProductResponseDto(productRepository.save(product));
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
