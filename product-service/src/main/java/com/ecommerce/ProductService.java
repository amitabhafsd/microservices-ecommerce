package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request) {
        Product product = mapToProduct(request);
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    public List<ProductResponse> createProducts(List<ProductRequest> requests) {

        List<Product> products = requests
                .stream()
                .map(this::mapToProduct)
                .toList();

        return productRepository.saveAll(products)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse getProduct(UUID id) {

        Product product = productRepository
                .findById(id)
                .orElseThrow();

        return mapToResponse(product);
    }

    public void deleteProduct(UUID id) {

        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    private Product mapToProduct(ProductRequest request) {

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
    }
}
