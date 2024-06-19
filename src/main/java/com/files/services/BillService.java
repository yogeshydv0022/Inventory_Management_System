package com.files.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.files.exception.BillNotFoundException;
import com.files.exception.CartEmptyException;
import com.files.exception.ProductOutOfStockException;
import com.files.exception.UserNotFoundException;
import com.files.model.Bill;
import com.files.model.Cart;
import com.files.model.CartItem;
import com.files.model.ProductBill;
import com.files.model.Products;
import com.files.model.User;
import com.files.payload.response.BillResponse;
import com.files.repository.BillRepository;
import com.files.repository.CartItemRepository;
import com.files.repository.CartRepository;
import com.files.repository.ProductRepository;
import com.files.repository.UserRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import jakarta.transaction.Transactional;

@Service
public class BillService {

	@Autowired
	private BillRepository billRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	
	@Transactional
	public BillResponse generateBill(Long userId, boolean paymentStatus) throws IOException {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("User not found"));

	    Cart cart = user.getCart();
	    if (cart == null || cart.getItems().isEmpty()) {
	        throw new CartEmptyException("Cart is empty");
	    }

	    List<ProductBill> productBills = new ArrayList<>();
	    for (CartItem item : cart.getItems()) {
	        productBills.add(new ProductBill(item.getProduct().getProductName(), item.getPrice(),
	                item.getQuantity(), item.getSubtotal()));
	    }

	    cart.calculateGrandTotal();
	    
	    
	    //Set Date or Time Format
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	   
	    
	    
	    // create a clock 
        Clock cl = Clock.systemDefaultZone(); 
        // create an LocalDate object using now(Clock) 
        LocalDate date  = LocalDate.now(cl);
     // create an LocalDateTime object using now(Clock) 
	    LocalDateTime generatedAt = LocalDateTime.now(cl);
	    String formattedDateTime = generatedAt.format(formatter);

	    
	    // Create a new Bill entity and save it to the database
	    Bill bill = new Bill();
	    bill.setProducts(productBills);
	    bill.setGrandTotal(cart.getGrandTotal());
	    bill.setGeneratedAt(formattedDateTime);
	    bill.setDate(date.toString());
	    bill.setPaymentStatus(paymentStatus);
	    bill = billRepository.save(bill);

	    // Update product quantities after bill generation
	    for (CartItem cartItem : cart.getItems()) {
	        Products product = cartItem.getProduct();
	        int quantity = cartItem.getQuantity();

	        if (product.getQuantity() < quantity) {
	            throw new ProductOutOfStockException("Not enough product in stock for product: " + product.getProductName());
	        }

	        product.setQuantity(product.getQuantity() - quantity);
	        productRepository.save(product);  // Save the product entity after updating the quantity
	    }

	    // Reset user's cart after bill generation
	    cartItemRepository.deleteAll(cart.getItems());
	    cartRepository.delete(cart);

	    user.setCart(null);
	    userRepository.save(user);


	    BillResponse billResponse = new BillResponse(productBills, cart.getGrandTotal(), generatedAt, bill.getId(), paymentStatus);
	    
	    return billResponse;
	}

	public Page<Bill> findBills(Long billId,LocalDate startDate,LocalDate endDate, boolean paymentStatus,
			Pageable pageable) {
		Specification<Bill> spec = Specification.where(null);

		if (billId != null) {
			spec = spec.and((root, query, builder) -> builder.equal(root.get("id"), billId));
		}

		if (startDate != null && endDate != null) {
			spec = spec.and((root, query, builder) -> builder.between(root.get("generatedAt"), startDate, endDate));
		}

		spec = spec.and((root, query, builder) -> builder.equal(root.get("paymentStatus"), paymentStatus));

		return billRepository.findAll(spec, pageable);
	}


	
	//generatePdfByBillId
	@Transactional
    public byte[] generatePdfForBillId(Long billId) throws IOException {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + billId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            // Add Invoice Title
            document.add(new Paragraph("Invoice").setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER));

            // Add Bill Details
            document.add(new Paragraph("Bill ID: " + bill.getId()).setFontSize(12).setBold()
                    .setTextAlignment(TextAlignment.LEFT));

            String generatedAt = bill.getGeneratedAt().formatted(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            document.add(new Paragraph("Generated At: " + generatedAt).setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));

            // Add Table with Bill Details
            float[] columnWidths = { 1, 2, 1, 1, 1 };
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

            table.addHeaderCell(new Paragraph("S.No").setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Paragraph("Product Name").setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Paragraph("Price").setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Paragraph("Quantity").setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Paragraph("Subtotal").setBackgroundColor(ColorConstants.LIGHT_GRAY));

            List<ProductBill> productBills = bill.getProducts();
            for (int i = 0; i < productBills.size(); i++) {
                ProductBill productBill = productBills.get(i);
                table.addCell(String.valueOf(i + 1));
                table.addCell(productBill.getProductName());
                table.addCell(String.valueOf(productBill.getPrice()));
                table.addCell(String.valueOf(productBill.getQuantity()));
                table.addCell(String.valueOf(productBill.getSubtotal()));
            }

            table.addCell(new Paragraph("Total Amount:").setBold());
            table.addCell(new Paragraph("Rs " + bill.getGrandTotal()).setBold());

            document.add(table);

            // Add Payment Status
            document.add(new Paragraph("Payment Status: " + (bill.isPaymentStatus() ? "Paid" : "Pending"))
                    .setFontSize(12).setBold().setTextAlignment(TextAlignment.LEFT));

            // Add Page Break
            document.add(new Paragraph().add("\n"));
        }

        return baos.toByteArray();
    }

}
