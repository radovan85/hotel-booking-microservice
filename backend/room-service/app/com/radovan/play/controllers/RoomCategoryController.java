package com.radovan.play.controllers;

import com.radovan.play.dto.RoomCategoryDto;
import com.radovan.play.exceptions.DataNotValidatedException;
import com.radovan.play.security.JwtAuthAction;
import com.radovan.play.security.RoleSecured;
import com.radovan.play.services.RoomCategoryService;
import jakarta.inject.Inject;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

@With(JwtAuthAction.class)
public class RoomCategoryController extends Controller {

    private RoomCategoryService categoryService;
    private FormFactory formFactory;

    @Inject
    private void initialize(RoomCategoryService categoryService, FormFactory formFactory) {
        this.categoryService = categoryService;
        this.formFactory = formFactory;
    }

    public Result getAllCategories(){
        return ok(Json.toJson(categoryService.listAll()));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result saveCategory(Http.Request request){
        Form<RoomCategoryDto> categoryForm = formFactory.form(RoomCategoryDto.class).bindFromRequest(request);
        if (categoryForm.hasErrors()) {
            throw new DataNotValidatedException("Room category data is not valid!");
        }

        RoomCategoryDto category = categoryForm.get();
        RoomCategoryDto storedCategory = categoryService.addCategory(category);
        return ok(Json.toJson("Room category with id " + storedCategory.getRoomCategoryId() + " has been stored!"));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result updateCategory(Http.Request request, Integer categoryId){
        Form<RoomCategoryDto> categoryForm = formFactory.form(RoomCategoryDto.class).bindFromRequest(request);
        if (categoryForm.hasErrors()) {
            throw new DataNotValidatedException("Room category data is not valid!");
        }

        RoomCategoryDto category = categoryForm.get();
        RoomCategoryDto updatedCategory = categoryService.updateCategory(category, categoryId);
        return ok(Json.toJson("Room category with id " + updatedCategory.getRoomCategoryId() + " has been updated without any issues!"));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result deleteCategory(Integer categoryId){
        categoryService.deleteCategory(categoryId);
        return ok(Json.toJson("Room category with id " + categoryId + " has been permanently deleted!"));
    }

    public Result getCategoryDetails(Integer categoryId){
        return ok(Json.toJson(categoryService.getCategoryById(categoryId)));
    }


}
