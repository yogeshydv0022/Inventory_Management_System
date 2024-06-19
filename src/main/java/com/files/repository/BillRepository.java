package com.files.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.files.model.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

	Page<Bill> findAll(Specification<Bill> spec, Pageable pageable);
}
