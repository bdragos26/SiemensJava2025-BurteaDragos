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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

        ResponseEntity<List<Item>> response = itemController.getAllItems();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCreateItem() {
        MockitoAnnotations.openMocks(this);

        Item item = new Item(null, "NewItem", "NewDescription", null, "newitem@example.com");
        Item savedItem = new Item(1L, "NewItem", "NewDescription", null, "newitem@example.com");

        when(itemService.save(item)).thenReturn(savedItem);
        when(bindingResult.hasErrors()).thenReturn(false); // No validation errors

        ResponseEntity<?> response = itemController.createItem(item, bindingResult);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(savedItem, response.getBody());
    }

    @Test
    void testCreateItemWithInvalidEmail() {
        MockitoAnnotations.openMocks(this);

        // Mock data with an invalid email
        Item item = new Item(null, "NewItem", "NewDescription", null, "invalid-email");

        when(bindingResult.hasErrors()).thenReturn(true); // Simulate validation errors

        ResponseEntity<?> response = itemController.createItem(item, bindingResult);
        assertEquals(400, response.getStatusCodeValue()); // Expecting BAD_REQUEST
    }

    @Test
    void testGetItemByIdFound() {
        MockitoAnnotations.openMocks(this);

        Item item = new Item(1L, "Item1", "Description1", "PROCESSED", "item1@example.com");
        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item, response.getBody());
    }

    @Test
    void testGetItemByIdNotFound() {
        MockitoAnnotations.openMocks(this);

        when(itemService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateItemSuccess() {
        MockitoAnnotations.openMocks(this);

        Item item = new Item(null, "UpdatedItem", "UpdatedDescription", null, "updated@example.com");
        Item updatedItem = new Item(1L, "UpdatedItem", "UpdatedDescription", null, "updated@example.com");

        when(itemService.findById(1L)).thenReturn(Optional.of(updatedItem));
        when(itemService.save(item)).thenReturn(updatedItem);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = itemController.updateItem(1L, item, bindingResult);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedItem, response.getBody());
    }

    @Test
    void testUpdateItemValidationError() {
        MockitoAnnotations.openMocks(this);

        Item item = new Item(null, "UpdatedItem", "UpdatedDescription", null, "invalid-email");

        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = itemController.updateItem(1L, item, bindingResult);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testUpdateItemNotFound() {
        MockitoAnnotations.openMocks(this);

        Item item = new Item(null, "UpdatedItem", "UpdatedDescription", null, "updated@example.com");

        when(itemService.findById(1L)).thenReturn(Optional.empty());
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = itemController.updateItem(1L, item, bindingResult);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteItemSuccess() {
        MockitoAnnotations.openMocks(this);

        when(itemService.findById(1L)).thenReturn(Optional.of(new Item()));

        ResponseEntity<Void> response = itemController.deleteItem(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(itemService, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteItemNotFound() {
        MockitoAnnotations.openMocks(this);

        when(itemService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = itemController.deleteItem(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testProcessItemsSuccess() throws Exception {
        MockitoAnnotations.openMocks(this);

        List<Item> processedItems = Arrays.asList(
                new Item(1L, "Item1", "Description1", "PROCESSED", "item1@example.com"),
                new Item(2L, "Item2", "Description2", "PROCESSED", "item2@example.com")
        );

        when(itemService.processItemsAsync()).thenReturn(CompletableFuture.completedFuture(processedItems));

        ResponseEntity<List<Item>> response = itemController.processItems();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(processedItems, response.getBody());
    }

    @Test
    void testProcessItemsException() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(itemService.processItemsAsync()).thenThrow(new RuntimeException("Processing error"));

        ResponseEntity<List<Item>> response = itemController.processItems();
        assertEquals(500, response.getStatusCodeValue());
    }
}