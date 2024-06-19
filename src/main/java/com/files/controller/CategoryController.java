package com.files.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.files.model.Category;
import com.files.services.CategoryService;

@RestController
@RequestMapping("/ims/category")
public class CategoryController {
	
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private PagedResourcesAssembler<Category> pagedResourcesAssembler;

    //Create Category
    @PostMapping("/")
    public ResponseEntity<?> addCategory(@RequestPart("category") Category category, @RequestPart("image") MultipartFile image) throws IOException {
        try {
        	Category response=categoryService.addCategory(category, image);
            return ResponseEntity.ok(response);
 	    } catch (Exception e) {
 	        return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
 	    }      
    }


    //get Category By Id
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
        	Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
 	    } catch (Exception e) {
 	        return new ResponseEntity<String>(e.getMessage()+"", HttpStatus.NOT_FOUND);
 	    }
    }

    

    //update Category by Id
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestPart Category category, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
    	try {
        	Category response = categoryService.updateCategory(id, category, image);
            return ResponseEntity.ok(response);
 	    } catch (Exception e) {
 	        return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_MODIFIED);
 	    }
    }

    //delete Category by Id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
        	String response = categoryService.deleteCategory(id);;
            return ResponseEntity.ok(response);
 	    } catch (Exception e) {
 	        return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
 	    }
    }

    //delete Category With All product By Id
    @DeleteMapping("products/{id}")
    public ResponseEntity<?> deleteCategoryWithProducts(@PathVariable Long id) {
        try {
        	String response = categoryService.deleteCategoryWithProducts(id);
            return ResponseEntity.ok(response);
 	    } catch (Exception e) {
 	        return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
 	    }
        
    }

    //get All Category version 1
    @GetMapping("/v1/")
    public ResponseEntity<?>  getAllCategories() { 
        try {
        	List<Category> response = categoryService.getAllCategories();
            return ResponseEntity.ok(response);
 	    } catch (Exception e) {
 	        return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
 	    }
    }
     
  //get All Category version 2
    @GetMapping("/v2/")
    public PagedModel<EntityModel<Category>> findAllCategories(@PageableDefault(page = 0, size = 5)Pageable pageable) { 
        try {
        	 Page<Category> productPage = categoryService.findAllCategory(pageable);
             return pagedResourcesAssembler.toModel(productPage);
 	    } catch (Exception e) {
 	    	 throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
 	    }  
    }
}
