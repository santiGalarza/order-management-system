package com.santiGalarza.order_management.product;

import com.santiGalarza.order_management.category.Category;
import com.santiGalarza.order_management.category.CategoryRepository;
import com.santiGalarza.order_management.category.exception.CategoryNotFoundException;
import com.santiGalarza.order_management.product.dto.CreateProductRequest;
import com.santiGalarza.order_management.product.dto.PatchProductRequest;
import com.santiGalarza.order_management.product.dto.ProductResponse;
import com.santiGalarza.order_management.product.dto.UpdateProductRequest;
import com.santiGalarza.order_management.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.create("Category", "Description", true, null);
        category.setId(UUID.randomUUID());
    }

    private Product someProduct() {
        return Product.create("Product", BigDecimal.valueOf(10.00), "SKU123456", 1, 100, 0.5f, category, true);
    }

    private ProductResponse someProductResponse() {
        return new ProductResponse(UUID.randomUUID(), "Product", BigDecimal.TEN, "SKU123456",
                category.getId(), category.getName(), true, 1, 100, 0.5f, LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("getProduct")
    class GetProduct {

        @Test
        @DisplayName("returns the mapped product when found")
        void returnsMappedProductWhenFound() {
            Product product = someProduct();
            ProductResponse response = someProductResponse();
            UUID id = UUID.randomUUID();

            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            when(productMapper.toProductResponse(product)).thenReturn(response);

            ProductResponse result = productService.getProduct(id);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when product is not found")
        void throwsWhenProductNotFound() {
            UUID id = UUID.randomUUID();

            when(productRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProduct(id))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getProducts")
    class GetProducts {

        @Test
        @DisplayName("returns all products mapped to response dtos")
        void returnsAllMappedProducts() {
            Product product = someProduct();
            ProductResponse response = someProductResponse();

            when(productRepository.findAll()).thenReturn(List.of(product));
            when(productMapper.toProductResponse(product)).thenReturn(response);

            List<ProductResponse> result = productService.getProducts();

            assertThat(result).containsExactly(response);
        }
    }

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("creates the product, sets the category, and returns the mapped result")
        void createsProductAndSetsCategory() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Product");
            request.setPrice(BigDecimal.valueOf(10.00));
            request.setSku("SKU123456");
            request.setIsActive(true);
            request.setMinOrderQuantity(1);
            request.setStockQuantity(100);
            request.setWeight(BigDecimal.valueOf(0.5));
            request.setCategoryId(category.getId());

            Product productEntity = Product.create("Product", BigDecimal.valueOf(10.00), "SKU123456", 1, 100, 0.5f, null, true);
            Product savedProduct = someProduct();
            ProductResponse response = someProductResponse();

            when(productMapper.createProductRequestToProduct(request)).thenReturn(productEntity);
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(productRepository.save(productEntity)).thenReturn(savedProduct);
            when(productMapper.toProductResponse(savedProduct)).thenReturn(response);

            ProductResponse result = productService.createProduct(request);

            assertThat(productEntity.getCategory()).isEqualTo(category);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when the category is not found")
        void throwsWhenCategoryNotFound() {
            UUID categoryId = UUID.randomUUID();

            CreateProductRequest request = new CreateProductRequest();
            request.setCategoryId(categoryId);

            Product productEntity = Product.create("Product", BigDecimal.valueOf(10.00), "SKU123456", 1, 100, 0.5f, null, true);

            when(productMapper.createProductRequestToProduct(request)).thenReturn(productEntity);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.createProduct(request))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("replaceProduct")
    class ReplaceProduct {

        @Test
        @DisplayName("replaces the product, updates the category, and returns the mapped result")
        void replacesProductAndUpdatesCategory() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();

            UpdateProductRequest request = new UpdateProductRequest();
            request.setName("Updated");
            request.setPrice(BigDecimal.valueOf(20.00));
            request.setIsActive(false);
            request.setMinOrderQuantity(2);
            request.setStockQuantity(50);
            request.setWeight(1.0f);
            request.setCategoryId(category.getId());

            ProductResponse response = someProductResponse();

            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(response);

            ProductResponse result = productService.replaceProduct(id, request);

            verify(productMapper).updateProductFromRequest(request, product);
            assertThat(product.getCategory()).isEqualTo(category);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when product is not found")
        void throwsWhenProductNotFound() {
            UUID id = UUID.randomUUID();
            UpdateProductRequest request = new UpdateProductRequest();

            when(productRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.replaceProduct(id, request))
                    .isInstanceOf(ProductNotFoundException.class);
        }

        @Test
        @DisplayName("throws when the category is not found")
        void throwsWhenCategoryNotFound() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();

            UpdateProductRequest request = new UpdateProductRequest();
            request.setCategoryId(categoryId);

            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.replaceProduct(id, request))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("patches the product without touching the category when categoryId is null")
        void patchesProductWithoutCategoryChange() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();

            PatchProductRequest request = new PatchProductRequest();
            request.setName("Updated");

            ProductResponse response = someProductResponse();

            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(response);

            ProductResponse result = productService.updateProduct(id, request);

            verify(productMapper).patchProductFromRequest(request, product);
            assertThat(product.getCategory()).isEqualTo(category);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("patches the product and updates the category when categoryId is provided")
        void patchesProductWithCategoryChange() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();

            Category newCategory = Category.create("New", "New description", true, null);
            newCategory.setId(UUID.randomUUID());

            PatchProductRequest request = new PatchProductRequest();
            request.setCategoryId(newCategory.getId());

            ProductResponse response = someProductResponse();

            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(response);

            ProductResponse result = productService.updateProduct(id, request);

            assertThat(product.getCategory()).isEqualTo(newCategory);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when product is not found")
        void throwsWhenProductNotFound() {
            UUID id = UUID.randomUUID();
            PatchProductRequest request = new PatchProductRequest();

            when(productRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(id, request))
                    .isInstanceOf(ProductNotFoundException.class);
        }

        @Test
        @DisplayName("throws when the category is not found")
        void throwsWhenCategoryNotFound() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();

            PatchProductRequest request = new PatchProductRequest();
            request.setCategoryId(categoryId);

            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(id, request))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteProduct")
    class DeleteProduct {

        @Test
        @DisplayName("deletes the product when found")
        void deletesProductWhenFound() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();

            when(productRepository.findById(id)).thenReturn(Optional.of(product));

            productService.deleteProduct(id);

            verify(productRepository).delete(product);
        }

        @Test
        @DisplayName("throws when product is not found")
        void throwsWhenProductNotFound() {
            UUID id = UUID.randomUUID();

            when(productRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deleteProduct(id))
                    .isInstanceOf(ProductNotFoundException.class);

            verify(productRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("getProductsByIds")
    class GetProductsByIds {

        @Test
        @DisplayName("returns a map of products keyed by id when all are found")
        void returnsMapWhenAllFound() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();
            product.setId(id);

            when(productRepository.findAllById(List.of(id))).thenReturn(List.of(product));

            Map<UUID, Product> result = productService.getProductsByIds(List.of(id));

            assertThat(result).containsEntry(id, product);
        }

        @Test
        @DisplayName("throws when some products are not found")
        void throwsWhenSomeProductsNotFound() {
            Product product = someProduct();
            UUID foundId = UUID.randomUUID();
            UUID missingId = UUID.randomUUID();
            product.setId(foundId);

            when(productRepository.findAllById(List.of(foundId, missingId))).thenReturn(List.of(product));

            assertThatThrownBy(() -> productService.getProductsByIds(List.of(foundId, missingId)))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findProduct")
    class FindProduct {

        @Test
        @DisplayName("returns the product when found")
        void returnsProductWhenFound() {
            Product product = someProduct();
            UUID id = UUID.randomUUID();

            when(productRepository.findById(id)).thenReturn(Optional.of(product));

            Product result = productService.findProduct(id);

            assertThat(result).isEqualTo(product);
        }

        @Test
        @DisplayName("throws when product is not found")
        void throwsWhenProductNotFound() {
            UUID id = UUID.randomUUID();

            when(productRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findProduct(id))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }
}
