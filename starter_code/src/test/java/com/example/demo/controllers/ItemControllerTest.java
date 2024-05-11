package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemControllerTest {
    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }


    @Test
    void getItemsTest() {
        List<Item> items = TestUtils.createItems();
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());


        List<Item> itemRetrieved = responseEntity.getBody();
        assertNotNull(itemRetrieved);
        assertEquals(5, itemRetrieved.size());

    }

    @Test
    void getItemByIdTest() {
        Item item = TestUtils.createItem(1L);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ResponseEntity<Item> responseEntity = itemController.getItemById(item.getId());
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Item retrievedItem = responseEntity.getBody();
        Item compareItem = TestUtils.createItem(1L);
        assert retrievedItem != null;
        assertEquals(compareItem.getName(), retrievedItem.getName());
        assertEquals(compareItem.getId(), retrievedItem.getId());
        assertEquals(compareItem.getDescription(), retrievedItem.getDescription());

    }

    @Test
    void getItemsByNameTest() {
        String itemName = "Round Widget";
        List<Item> items = TestUtils.createItems();
        when(itemRepository.findByName(itemName)).thenReturn(items);

        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(itemName);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        List<Item> retrievedItems = responseEntity.getBody();
        assertNotNull(retrievedItems);
        assertEquals(5, retrievedItems.size());
        assertEquals(items.get(0), retrievedItems.get(0));
    }
}
