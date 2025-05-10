package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up a custom executor for testing
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.initialize();

        // Inject the custom executor into the already initialized itemService
        itemService.executor = executor;
    }

    @Test
    void testFindAll() {
        List<Item> items = Arrays.asList(
                new Item(1L, "Item1", "Description1", null, "item1@example.com"),
                new Item(2L, "Item2", "Description2", null, "item2@example.com")
        );

        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.findAll();

        assertEquals(2, result.size());
        assertEquals(items, result);
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSave() {
        Item item = new Item(null, "NewItem", "NewDescription", null, "newitem@example.com");
        Item savedItem = new Item(1L, "NewItem", "NewDescription", null, "newitem@example.com");

        when(itemRepository.save(item)).thenReturn(savedItem);

        Item result = itemService.save(item);

        assertEquals(savedItem, result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testDeleteById() {
        itemService.deleteById(1L);
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByIdFound() {
        Item item = new Item(1L, "Item1", "Description1", null, "item1@example.com");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(item, result.get());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Item> result = itemService.findById(1L);

        assertFalse(result.isPresent());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testProcessItemsAsync() throws Exception {
        Item item1 = new Item(1L, "Item1", "Description1", null, "item1@example.com");
        Item item2 = new Item(2L, "Item2", "Description2", null, "item2@example.com");
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> processedItems = future.get();

        assertEquals(2, processedItems.size());
        assertEquals("PROCESSED", processedItems.get(0).getStatus());
        assertEquals("PROCESSED", processedItems.get(1).getStatus());

        verify(itemRepository, times(2)).save(any(Item.class));
    }

    @Test
    void testProcessItemsAsyncWithAlreadyProcessedItems() throws Exception {
        Item item1 = new Item(1L, "Item1", "Description1", "PROCESSED", "item1@example.com");
        Item item2 = new Item(2L, "Item2", "Description2", null, "item2@example.com");
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> processedItems = future.get();

        assertEquals(1, processedItems.size());
        assertEquals("PROCESSED", processedItems.get(0).getStatus());
        assertEquals(item2.getId(), processedItems.get(0).getId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testProcessItemsAsyncWithException() {
        // Mock repository behavior
        when(itemRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> itemService.processItemsAsync().get());
        verify(itemRepository, times(1)).findAll();
    }
}