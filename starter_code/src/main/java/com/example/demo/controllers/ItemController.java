package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	private final Logger logger = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		logger.info("Request received to retrieve all items.");
		List<Item> items = itemRepository.findAll();
		logger.info("Retrieved {} items.", items.size());
		return ResponseEntity.ok(items);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		logger.info("Request received to retrieve item by ID: {}", id);
		Optional<Item> optionalItem = itemRepository.findById(id);
		if (optionalItem.isPresent()) {
			logger.info("Item found with ID: {}", id);
			return ResponseEntity.ok(optionalItem.get());
		} else {
			logger.warn("Item not found with ID: {}", id);
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		logger.info("Request received to retrieve items by name: {}", name);
		List<Item> items = itemRepository.findByName(name);
		if (items == null || items.isEmpty()) {
			logger.warn("No items found with name: {}", name);
			return ResponseEntity.notFound().build();
		} else {
			logger.info("Retrieved {} items with name: {}", items.size(), name);
			return ResponseEntity.ok(items);
		}

	}
	
}
