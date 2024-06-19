package com.files.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.files.model.Notification;
import com.files.model.Products;
import com.files.repository.NotificationRepository;
import com.files.repository.ProductRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productsRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.to}")
    private String notificationEmailTo ;
    
    @Value("${spring.mail.username}")
	private String sender;
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private static final int LOW_INVENTORY_THRESHOLD = 10;

    //check Inventry Level = 1
    public void checkInventoryLevels() {
        List<Products> productsList = productsRepository.findAll();
        System.out.println("checkInventoryLevels = 1");
        logger.info("Checking inventory levels...");
        for (Products product : productsList) {
            if (product.getQuantity() < LOW_INVENTORY_THRESHOLD) {
                sendLowInventoryNotification(product);
            }
        }
    }

    
  //sendLowInventoryNotification  = 2
    private void sendLowInventoryNotification(Products product) {
        Notification notification = new Notification();
        System.out.println("sendLowInventoryNotification = 2");
        logger.info("Sending low inventory notification for product: {}", product.getProductName());
        notification.setProduct(product);
        notification.setMessage("Inventory for product " + product.getProductName() + " is low: " + product.getQuantity() + " items left.");
        notificationRepository.save(notification);
        sendEmailNotification(notification);
    }
    
    //sendEmailNotification =3
    private void sendEmailNotification(Notification notification) {
    	 try {
    		 SimpleMailMessage message = new SimpleMailMessage();
        System.out.println("sendEmailNotification = 3");
        logger.info("Preparing email notification...");
        message.setFrom(sender);
        message.setTo(notificationEmailTo);
        message.setSubject("Low Inventory Alert");
        message.setText(notification.getMessage());
        mailSender.send(message);
            logger.info("Email sent successfully to {}", notificationEmailTo);
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
}

