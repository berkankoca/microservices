package com.koca.inventoryservice.controller;

import com.koca.inventoryservice.dto.InventoryStockResponse;
import com.koca.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/code/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku-code") String skuCode) {
        return inventoryService.isInStock(skuCode);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryStockResponse> isInStock(@RequestParam("skuCode") List<String> skuCodes) {
        return inventoryService.isInStock(skuCodes);
    }

}
