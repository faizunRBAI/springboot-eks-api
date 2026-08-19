package com.example.springbooteksapi.api;

import com.example.springbooteksapi.domain.Item;
import com.example.springbooteksapi.domain.ItemRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD surface for {@link Item} — extend with POST/PUT/DELETE as needed.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository items;

    public ItemController(final ItemRepository items) {
        this.items = items;
    }

    /**
     * List all items.
     *
     * @return list of all persisted items
     */
    @GetMapping
    public ResponseEntity<List<Item>> list() {
        return ResponseEntity.ok(items.findAll());
    }
}
