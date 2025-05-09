package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ItemController itemController;

    @Test
    void testGetAllItems() {
        MockitoAnnotations.openMocks(this);

        // Mock data
        List<Item> items = Arrays.asList(
                new Item(1L, "Item1", "Description1", "PROCESSED", "item1@example.com"),
                new Item(2L, "Item2", "Description2", "PENDING", "item2@example.com")
        );

        when(itemService.findAll()).thenReturn(items);

        // Call the method
        ResponseEntity<List<Item>> response = itemController.getAllItems();

        // Verify the results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCreateItem() {
        MockitoAnnotations.openMocks(this);

        // Mock data
        Item item = new Item(null, "NewItem", "NewDescription", null, "newitem@example.com");
        Item savedItem = new Item(1L, "NewItem", "NewDescription", null, "newitem@example.com");

        when(itemService.save(item)).thenReturn(savedItem);
        when(bindingResult.hasErrors()).thenReturn(false); // No validation errors

        // Call the method
        ResponseEntity<?> response = itemController.createItem(item, bindingResult);

        // Verify the results
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(savedItem, response.getBody());
    }

    @Test
    void testCreateItemWithInvalidEmail() {
        MockitoAnnotations.openMocks(this);

        // Mock data with an invalid email
        Item item = new Item(null, "NewItem", "NewDescription", null, "invalid-email");

        when(bindingResult.hasErrors()).thenReturn(true); // Simulate validation errors

        // Call the method
        ResponseEntity<?> response = itemController.createItem(item, bindingResult);

        // Verify the results
        assertEquals(400, response.getStatusCodeValue()); // Expecting BAD_REQUEST
    }
}