package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    @Qualifier("customTaskExecutor") // custom executor
    private Executor executor; // Use Spring-managed executor for better resource management

    // Asynchronous method to process items
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        // Thread-safe collection to store processed items
        List<Item> processedItems = new CopyOnWriteArrayList<>();

        // Retrieve all item IDs from the database
        List<Long> itemIds = itemRepository.findAllIds();

        // Create a list of CompletableFuture tasks
        List<CompletableFuture<Void>> futures = itemIds.stream()
                .map(id -> CompletableFuture.runAsync(() -> {
                    try {
                        // Retrieve the item by ID
                        Item item = itemRepository.findById(id).orElse(null);
                        if (item != null) {
                            // Update the item's status to "PROCESSED"
                            item.setStatus("PROCESSED");
                            // Save the updated item back to the database
                            itemRepository.save(item);
                            // Add the item to the processed list
                            processedItems.add(item);
                        }
                    } catch (Exception e) {
                        // Log any errors that occur during processing
                        System.err.println("Error processing item with ID " + id + ": " + e.getMessage());
                    }
                }, executor))
                .toList();

        // Wait for all tasks to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // Return the list of processed items once all tasks are complete
        return allOf.thenApply(v -> processedItems);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }
}