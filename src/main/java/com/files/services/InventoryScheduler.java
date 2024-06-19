package com.files.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InventoryScheduler {

    @Autowired
    private InventoryService inventoryService;

    @Scheduled(fixedRate = 1000*60*60) // Check inventory every hour
    public void checkInventory() {
        inventoryService.checkInventoryLevels();
    }
}