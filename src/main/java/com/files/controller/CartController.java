package com.files.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.files.payload.request.CartProductRequest;
import com.files.services.CartService;

@RestController
@RequestMapping("/ims/cart")
public class CartController {
	
	@Autowired
    private CartService cartService;

	//Add Product In Cart
    @PostMapping("/{userId}")
    public ResponseEntity<String> addToCart(@PathVariable("userId") Long userId, @RequestBody List<CartProductRequest> products) {
        String message = cartService.addToCart(userId, products);
        return ResponseEntity.ok(message);
    }

    //delete Product In Cart
    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long userId, @PathVariable Long cartItemId) {
        String message = cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(message);
    }

    //update Product In Cart
    @PutMapping("/update/{userId}/{cartItemId}/{quantity}")
    public ResponseEntity<String> updateCartItem(@PathVariable Long userId, @PathVariable Long cartItemId, @PathVariable int quantity) {
        String message = cartService.updateCartItem(userId, cartItemId, quantity);
        return ResponseEntity.ok(message);
    }

}
