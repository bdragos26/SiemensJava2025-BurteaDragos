package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item, BindingResult result) {
        // Check for validation errors
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        // If validation passes, save the item and return CREATED status
        return new ResponseEntity<>(itemService.save(item), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        // Return OK if the item is found, otherwise return NOT_FOUND
        return itemService.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody Item item, BindingResult result) {
        // Verifies if there are validation errors in the request body.
        // If errors exist, returns a BAD_REQUEST response with the list of validation errors.
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id);
            // Return OK if the item is successfully updated
            return new ResponseEntity<>(itemService.save(item), HttpStatus.OK);
        } else {
            // Return NOT_FOUND if the item does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        // Check if the item exists before attempting to delete
        if (itemService.findById(id).isPresent()) {
            // Delete the item and return NO_CONTENT if successful
            itemService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Return NOT_FOUND if the item does not exist
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/process")
    public ResponseEntity<List<Item>> processItems() {
        try {
            // Wait for the CompletableFuture to complete and get the result
            List<Item> processedItems = itemService.processItemsAsync().get();
            return new ResponseEntity<>(processedItems, HttpStatus.OK);
        } catch (Exception e) {
            // Handle any exceptions that occur during processing
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}