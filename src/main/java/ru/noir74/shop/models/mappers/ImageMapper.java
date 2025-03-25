package ru.noir74.shop.models.mappers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.noir74.shop.models.domain.Image;
import ru.noir74.shop.models.entity.ImageEntity;
import ru.noir74.shop.repositories.ItemRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ImageMapper {
    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;

    public ImageEntity domain2entity(Image domain) {
        var entity = Optional.ofNullable(domain)
                .map(obj -> modelMapper.map(obj, ImageEntity.class))
                .orElse(null);
        Optional.ofNullable(entity).ifPresent(obj ->
                obj.setItemEntity(itemRepository.findById(domain.getItemId()).orElse(null))
        );
        return entity;
    }

    public Image entity2domain(ImageEntity entity) {
        var domain = Optional.ofNullable(entity)
                .map(obj -> modelMapper.map(obj, Image.class))
                .orElse(null);
        Optional.ofNullable(domain).ifPresent(obj -> obj.setItemId(entity.getItemEntity().getId()));
        return domain;
    }

}
