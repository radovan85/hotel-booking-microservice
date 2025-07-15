package com.radovan.play.services.impl;

import com.radovan.play.converter.TempConverter;
import com.radovan.play.dto.RoomCategoryDto;
import com.radovan.play.dto.RoomDto;
import com.radovan.play.entity.RoomCategoryEntity;
import com.radovan.play.exceptions.ExistingInstanceException;
import com.radovan.play.exceptions.InstanceUndefinedException;
import com.radovan.play.repositories.RoomCategoryRepository;
import com.radovan.play.services.RoomCategoryService;
import com.radovan.play.services.RoomService;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import play.mvc.Http;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class RoomCategoryServiceImpl implements RoomCategoryService {

    private RoomCategoryRepository categoryRepository;
    private TempConverter tempConverter;
    private Provider<RoomService> roomServiceProvider; // Lazy injection — resolves circular dependency

    @Inject
    private void initialize(RoomCategoryRepository categoryRepository,
                            TempConverter tempConverter,
                            Provider<RoomService> roomServiceProvider) {
        this.categoryRepository = categoryRepository;
        this.tempConverter = tempConverter;
        this.roomServiceProvider = roomServiceProvider;
    }

    @Override
    public RoomCategoryDto addCategory(RoomCategoryDto category) {
        Optional<RoomCategoryEntity> categoryOptional = categoryRepository.findByName(category.getName());
        categoryOptional.ifPresent(tempCategory -> {
            throw new ExistingInstanceException("This category exists already!");
        });

        RoomCategoryEntity storedCategory = categoryRepository.save(tempConverter.roomCategoryDtoToEntity(category));
        return tempConverter.roomCategoryEntityToDto(storedCategory);
    }

    @Override
    public RoomCategoryDto getCategoryById(Integer categoryId) {
        RoomCategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new InstanceUndefinedException("The category has not been found!"));
        return tempConverter.roomCategoryEntityToDto(categoryEntity);
    }

    @Override
    public RoomCategoryDto updateCategory(RoomCategoryDto category, Integer categoryId) {
        RoomCategoryDto currentCategory = getCategoryById(categoryId);
        Optional<RoomCategoryEntity> categoryOptional = categoryRepository.findByName(category.getName());
        categoryOptional.ifPresent(tempCategory -> {
            if(!Objects.equals(tempCategory.getRoomCategoryId(), categoryId)){
                throw new ExistingInstanceException("This category exists already!");
            }
        });

        category.setRoomCategoryId(currentCategory.getRoomCategoryId());
        if(currentCategory.getRoomsIds() != null){
            category.setRoomsIds(currentCategory.getRoomsIds());
        }
        RoomCategoryEntity updatedCategory = categoryRepository.save(tempConverter.roomCategoryDtoToEntity(category));
        return tempConverter.roomCategoryEntityToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        getCategoryById(categoryId);
        List<RoomDto> allRooms = roomServiceProvider.get().listAllByCategoryId(categoryId);
        allRooms.forEach(room -> roomServiceProvider.get().deleteRoom(room.getRoomId()));
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<RoomCategoryDto> listAll() {
        List<RoomCategoryEntity> allCategories = categoryRepository.findAll();
        return allCategories.stream().map(tempConverter::roomCategoryEntityToDto).collect(Collectors.toList());
    }
}
