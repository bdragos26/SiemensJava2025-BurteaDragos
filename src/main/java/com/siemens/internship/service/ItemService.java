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
    Executor executor; // Use Spring-managed executor for better resource management

    // Asynchronous method to process items
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        List<Item> processedItems = new CopyOnWriteArrayList<>();

        List<Item> items = itemRepository.findAll(); // we don't need to query on the id's

        List<CompletableFuture<Void>> futures = items.stream()
                .map(item -> CompletableFuture.runAsync(() -> {
                    try {
                        // We process only if the item is not already processed
                        if (!"PROCESSED".equalsIgnoreCase(item.getStatus())) {
                            item.setStatus("PROCESSED");
                            Item saved = itemRepository.save(item);
                            processedItems.add(saved);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing item with ID " + item.getId() + ": " + e.getMessage());
                    }
                }, executor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> processedItems);
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