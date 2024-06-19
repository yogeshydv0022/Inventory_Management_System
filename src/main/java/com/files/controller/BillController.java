package com.files.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.files.model.Bill;
import com.files.payload.response.BillResponse;
import com.files.services.BillService;

@RestController
@RequestMapping("/ims/bills")
public class BillController {

	@Autowired
    private BillService billService;
	
	@Autowired
	private PagedResourcesAssembler<Bill> pagedResourcesAssembler;

	//generate Bill
	 @PostMapping("/")
	    public ResponseEntity<BillResponse> generateBill(@RequestParam Long userId, @RequestParam boolean paymentStatus) throws IOException {
	        BillResponse billResponse = billService.generateBill(userId, paymentStatus);
	        return ResponseEntity.status(HttpStatus.CREATED).body(billResponse);
	    }
	 
	 //get All Bills With Filtering By Id,Date,paymentStatus
	 @GetMapping("/")
	    public PagedModel<EntityModel<Bill>> findBills(
	            @RequestParam(required = false) Long billId,
	            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	            @RequestParam(required = false, defaultValue = "false") boolean paymentStatus,
	            @PageableDefault(page = 0, size = 10) Pageable pageable) {

	        Page<Bill> billPage = billService.findBills(billId, startDate, endDate, paymentStatus, pageable);
	        return pagedResourcesAssembler.toModel(billPage, bill -> EntityModel.of(bill,
	                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BillController.class).findBills(billId, startDate, endDate, paymentStatus, pageable)).withSelfRel()));  
    }
	 
	 //get bill By Id
}