package com.radovan.play.services;

import com.radovan.play.dto.RoomCategoryDto;

import java.util.List;

public interface RoomCategoryService {

    RoomCategoryDto addCategory(RoomCategoryDto category);

    RoomCategoryDto getCategoryById(Integer categoryId);

    RoomCategoryDto updateCategory(RoomCategoryDto category, Integer categoryId);

    void deleteCategory(Integer categoryId);

    List<RoomCategoryDto> listAll();
}