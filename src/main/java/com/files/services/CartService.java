package com.files.services;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.files.exception.CartEmptyException;
import com.files.exception.ProductNotFoundException;
import com.files.exception.ProductOutOfStockException;
import com.files.exception.UserNotFoundException;
import com.files.model.Bill;
import com.files.model.Cart;
import com.files.model.CartItem;
import com.files.model.ProductBill;
import com.files.model.Products;
import com.files.model.User;
import com.files.payload.request.CartProductRequest;
import com.files.payload.response.BillResponse;
import com.files.repository.BillRepository;
import com.files.repository.CartItemRepository;
import com.files.repository.CartRepository;
import com.files.repository.ProductRepository;
import com.files.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
	
		private final CartRepository cartRepository;
		private final CartItemRepository cartItemRepository;
		private final ProductRepository productRepository;
		private final UserRepository userRepository;
		private final BillRepository billRepository;
		
		@Autowired
		private PdfService pdfService;

		public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
				ProductRepository productRepository, UserRepository userRepository, BillRepository billRepository) {
			this.cartRepository = cartRepository;
			this.cartItemRepository = cartItemRepository;
			this.productRepository = productRepository;
			this.userRepository = userRepository;
			this.billRepository = billRepository;
		}

		@Transactional
		public String addToCart(Long userId, List<CartProductRequest> products) {
		    User user = userRepository.findById(userId)
		            .orElseThrow(() -> new UserNotFoundException("User not found"));

		    // Always start with a new cart for each request
		    Cart cart = new Cart(); 
		    double grandTotal = 0;

		    List<CartItem> cartItems = new ArrayList<>();

		    for (CartProductRequest productRequest : products) {
		        Long productId = productRequest.getProductId();
		        int quantity = productRequest.getQuantity();

		        Products product = productRepository.findById(productId)
		                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

		        if (product.getQuantity() <= 0) {
		            // Remove the product from database if quantity is zero
		            productRepository.delete(product);
		            throw new ProductOutOfStockException("Product " + product.getProductName() + " is out of stock");
		        } else if (product.getQuantity() < quantity) {
		            throw new ProductOutOfStockException("Product " + product.getProductName() + " has only " + product.getQuantity() + " left");
		        }

		        double price = product.getPrice();
		        CartItem cartItem = new CartItem();
		        cartItem.setProduct(product);
		        cartItem.setQuantity(quantity);
		        cartItem.setCart(cart);
		        cartItem.setPrice(price);
		        cartItem.setSubtotal(price * quantity);
		        cartItems.add(cartItem);

		        grandTotal += price * quantity;
		    }

		    cart.setItems(cartItems);
		    cart.setGrandTotal(grandTotal);

		    // Save cart and cart items first before adjusting product quantities
		    cartRepository.save(cart);
		    cartItems.forEach(cartItemRepository::save);

		    user.setCart(cart);
		    userRepository.save(user);

		    return "Products added to cart successfully";
		}



		@Transactional
		public String removeFromCart(Long userId, Long cartItemId) {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
			Cart cart = user.getCart();
			CartItem cartItem = cartItemRepository.findById(cartItemId)
					.orElseThrow(() -> new CartEmptyException("CartItem not found"));

			Products product = cartItem.getProduct();
			product.setQuantity(product.getQuantity() + cartItem.getQuantity());

			cart.getItems().remove(cartItem);
			cartItemRepository.delete(cartItem);
			productRepository.save(product);
			cartRepository.save(cart);

			return "Product removed from cart";
		}

		@Transactional
		public String updateCartItem(Long userId, Long cartItemId, int quantity) {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
			Cart cart = user.getCart();
			CartItem cartItem = cartItemRepository.findById(cartItemId)
					.orElseThrow(() -> new CartEmptyException("CartItem not found"));

			Products product = cartItem.getProduct();
			int oldQuantity = cartItem.getQuantity();

			if (product.getQuantity() + oldQuantity < quantity) {
				throw new ProductOutOfStockException("Not enough product in stock");
			}

			product.setQuantity(product.getQuantity() + oldQuantity - quantity);
			cartItem.setQuantity(quantity);
			cartItem.setSubtotal(cartItem.getPrice() * quantity);

			cartItemRepository.save(cartItem);
			productRepository.save(product);

			cart.calculateGrandTotal();
			cartRepository.save(cart);

			return "Cart item updated";
		}

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

		    // Generate PDF for the bill with payment status
		    pdfService.generateBillPdf(bill, paymentStatus);

		    BillResponse billResponse = new BillResponse(productBills, cart.getGrandTotal(), generatedAt, bill.getId(), paymentStatus);

		    // Log the response details
		    System.out.println("Bill ID: " + billResponse.getBillId());
		    System.out.println("Products: " + billResponse.getProducts());
		    System.out.println("Grand Total: " + billResponse.getGrandTotal());
		    System.out.println("Generated At: " + billResponse.getGeneratedAt());
		    System.out.println("Payment Status: " + billResponse.isPaymentStatus());

		    return billResponse;
		}

}
