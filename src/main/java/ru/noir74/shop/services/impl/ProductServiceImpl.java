package ru.noir74.shop.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.noir74.shop.misc.enums.ProductSorting;
import ru.noir74.shop.misc.error.exceptions.NotFoundException;
import ru.noir74.shop.misc.error.exceptions.ProductIsUsedException;
import ru.noir74.shop.models.domain.Image;
import ru.noir74.shop.models.domain.Product;
import ru.noir74.shop.models.mappers.ProductMapper;
import ru.noir74.shop.repositories.ItemRepository;
import ru.noir74.shop.repositories.ProductRepository;
import ru.noir74.shop.services.ImageService;
import ru.noir74.shop.services.ProductService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageService imageService;


    @Override
    @Transactional(readOnly = true)
    public List<Product> getPage(Integer page, Integer size, ProductSorting sort) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
        return productMapper.bulkEntity2domain(productRepository
                .findAll(pageable)
                .stream()
                .collect(Collectors.toCollection(LinkedList::new)));
    }

    @Override
    @Transactional(readOnly = true)
    public Product get(Long id) {
        return productMapper.entity2domain(productRepository.findById(id).orElseThrow(() -> new NotFoundException("product is not found", "id=" + id)));
    }

    @Override
    @Transactional
    public Product create(Product product) throws IOException {
        return save(product);
    }

    @Override
    @Transactional
    public void update(Product product) throws IOException {
        save(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (Optional.ofNullable(itemRepository.isProductUsesInItems(id)).isPresent()) {
            throw new ProductIsUsedException("product is used in some order(s)", "productId=" + id);
        } else {
            imageService.deleteById(id);
            productRepository.deleteById(id);
        }
    }

    @Transactional
    private Product save(Product product) throws IOException {
        MultipartFile file = product.getFile();
        product = productMapper.entity2domain(productRepository.save(productMapper.domain2entity(product)));
        saveImage(product.getId(), file);
        return product;
    }

    @Transactional
    private void saveImage(Long productId, MultipartFile file) throws IOException {
        if (Objects.nonNull(file)) {
            var image = Image.builder()
                    .productId(productId)
                    .image(file.getBytes())
                    .imageName(file.getOriginalFilename()).build();
            if (image.isImageReadyToBeSaved()) {
                image.setProductId(productId);
                imageService.setImage(image);
            }
        }
    }

}
