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
    void testProcessItemsAsync() throws Exception {
        // Mock data
        List<Long> itemIds = Arrays.asList(1L, 2L);
        Item item1 = new Item(1L, "Item1", "Description1", null, "item1@example.com");
        Item item2 = new Item(2L, "Item2", "Description2", null, "item2@example.com");

        when(itemRepository.findAllIds()).thenReturn(itemIds);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));

        // Call the method
        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> processedItems = future.get();

        // Verify the results
        assertEquals(2, processedItems.size());
        assertEquals("PROCESSED", processedItems.get(0).getStatus());
        assertEquals("PROCESSED", processedItems.get(1).getStatus());

        verify(itemRepository, times(2)).save(any(Item.class));
    }
}