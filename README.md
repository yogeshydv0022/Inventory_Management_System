# Inventory Management System (IMS)

Welcome to the Inventory Management System (IMS) project! This system is designed to facilitate efficient management of inventory, sales transactions, purchase orders, and more for businesses.

## Table of Contents

[![Watch the demo video](https://img.youtube.com/vi/dQw4w9WgXcQ/0.jpg)](https://github.com/yogeshydv0022/Inventory_Management_System/blob/main/screen%20short/Main.mp4)
IMS_Project
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── files/
│   │   │           ├── ImsProjectApplication.java
│   │   │           ├── SpringDataWebConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AdminController.java
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── BillController.java
│   │   │           │   ├── CartController.java
│   │   │           │   ├── CategoryController.java
│   │   │           │   ├── PdfController.java
│   │   │           │   ├── ProductController.java
│   │   │           │   ├── SalesController.java
│   │   │           │   ├── SuperAdminController.java
│   │   │           │   └── UserController.java
│   │   │           ├── exception/
│   │   │           │   ├── BillNotFoundException.java
│   │   │           │   ├── CartEmptyException.java
│   │   │           │   ├── FileNotSupportedException.java
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── ProductNotFoundException.java
│   │   │           │   ├── ProductOutOfStockException.java
│   │   │           │   ├── ResourceNotFoundException.java
│   │   │           │   └── UserNotFoundException.java
│   │   │           ├── jwt/
│   │   │           │   ├── JwtAuthFilter.java
│   │   │           │   └── JwtService.java
│   │   │           ├── model/
│   │   │           │   ├── Bill.java
│   │   │           │   ├── Cart.java
│   │   │           │   ├── CartItem.java
│   │   │           │   ├── Category.java
│   │   │           │   ├── Notification.java
│   │   │           │   ├── ProductBill.java
│   │   │           │   ├── Products.java
│   │   │           │   ├── Role.java
│   │   │           │   ├── Sales.java
│   │   │           │   └── User.java
│   │   │           ├── payload/
│   │   │           │   ├── ComparisonResponse.java
│   │   │           │   ├── ComparisonResult.java
│   │   │           │   ├── GrandTotalResult.java
│   │   │           │   ├── SalesDTO.java
│   │   │           │   └── SalesRecord.java
│   │   │           ├── payload/request/
│   │   │           │   ├── CartProductRequest.java
│   │   │           │   ├── LoginRequest.java
│   │   │           │   └── RegisterRequest.java
│   │   │           ├── payload/response/
│   │   │           │   ├── AuthResponse.java
│   │   │           │   ├── BillResponse.java
│   │   │           │   └── UserResponse.java
│   │   │           ├── repository/
│   │   │           │   ├── BillRepository.java
│   │   │           │   ├── CartItemRepository.java
│   │   │           │   ├── CartRepository.java
│   │   │           │   ├── CategoryRepository.java
│   │   │           │   ├── NotificationRepository.java
│   │   │           │   ├── ProductRepository.java
│   │   │           │   ├── SalesRepository.java
│   │   │           │   └── UserRepository.java
│   │   │           ├── securityConfig/
│   │   │           │   ├── OpenApiConfig.java
│   │   │           │   └── SecurityConfig.java
│   │   │           ├── securityService/
│   │   │           │   └── UserDetailsServiceImpl.java
│   │   │           ├── service/
│   │   │           │   ├── Interface/
│   │   │           │   │   ├── ICategory.java
│   │   │           │   │   └── IProduct.java
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── BillService.java
│   │   │           │   ├── CartService.java
│   │   │           │   ├── CategoryService.java
│   │   │           │   ├── InventoryScheduler.java
│   │   │           │   ├── InventoryService.java
│   │   │           │   ├── PdfService.java
│   │   │           │   ├── ProductService.java
│   │   │           │   └── SalesService.java
│   │   ├── resources/
│   ├── test/
│   │   ├── java/
│   │   └── resources/
├── target/
│   ├── generated-sources/
│   │   └── annotations/
│   └── generated-test-sources/
│       └── test-annotations/
|── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml

## Features

1. **Product Management:**
   - Add, update, retrieve, and delete products.
   - Manage product details including name, brand, quantity, price, images, and category.

2. **Sales Management:**
   - Record sales transactions.
   - Update inventory levels after sales.
   - Generate invoices for customers.

3. **Purchase Order Management:**
   - Create, manage, and receive purchase orders.
   - Update inventory upon receiving shipments.

4. **Cart Management:**
   - Manage shopping carts with items.
   - Calculate total amounts and manage user-specific carts.

5. **Notification System:**
   - Notify users when inventory levels are low.
   - Send notifications related to sales, orders, and product updates.

6. **Reports and Insights:**
   - Generate reports on inventory status, sales performance, and supplier analysis.
   - Provide insights into business operations based on data analysis.

## Entity Diagram

Include an entity relationship diagram (ERD) here to visualize the database structure. This helps in understanding how entities (like Products, Category, Cart, etc.) are related and structured in your system.


![Entity Diagram](https://github.com/yogeshydv0022/Inventory_Management_System/blob/main/screen%20short/ERD.png)



## Setup Instructions

To run the Inventory Management System locally, follow these steps:

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yogeshydv0022/Inventory_Management_System.git
   cd Inventory_Management_System
Configure the database:

Set up your database configuration in application.properties (or application.yml if using YAML configuration).
Build and run the project:

bash
Copy code
mvn clean install
mvn spring-boot:run
Access the application:

Open your web browser and go to http://localhost:8080 to access the IMS application.

Usage
Use the IMS interface to manage products, sales, purchase orders, and notifications.
Explore various reports to gain insights into inventory status, sales performance, and more.


### Instructions:

1. **Replace placeholders:**
   - Replace `path/to/your/entity/diagram.png` with the actual path or URL to your entity diagram image file.

2. **Customize as needed:**
   - Modify feature descriptions, setup instructions, and additional notes based on your project's specific requirements and implementation details.

3. **Include database configuration:**
   - Ensure that the `application.properties` (or `application.yml`) file includes appropriate database connection settings.

Place this `README.md` file in the root directory of your project repository. It serves as a comprehensi
