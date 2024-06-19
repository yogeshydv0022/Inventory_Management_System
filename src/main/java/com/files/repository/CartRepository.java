package com.files.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.files.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

}
