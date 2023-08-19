package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.exception.ErrorHandler;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;


    @GetMapping
    public List<Item> getItems(@RequestHeader(USERID_HEADER) Long userId) {
        try {
            return itemService.getItems(userId);
        } catch (Exception e) {
            ErrorHandler.handleInternalServerError(e);
            return null;
        }
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Long itemId) {
        try {
            return itemService.getItem(itemId);
        } catch (Exception e) {
            ErrorHandler.handleInternalServerError(e);
            return null;
        }
    }

    @PostMapping
    public Item addItem(@RequestHeader(USERID_HEADER) Long userId, @RequestBody Item item) {
        try {
            return itemService.addItem(userId, item);
        } catch (Exception e) {
            ErrorHandler.handleInternalServerError(e);
            return null;
        }
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(USERID_HEADER) Long userId,
                           @PathVariable Long itemId,
                           @RequestBody Item item) {
        try {
            return itemService.updateItem(userId, itemId, item);
        } catch (Exception e) {
            ErrorHandler.handleInternalServerError(e);
            return null;
        }
    }

    @DeleteMapping("/{itemId}")
    public Boolean deleteItem(@PathVariable Long itemId) {
        try {
            return itemService.deleteItem(itemId);
        } catch (Exception e) {
            ErrorHandler.handleInternalServerError(e);
            return null;
        }
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        try {
            return itemService.searchItems(text);
        } catch (Exception e) {
            ErrorHandler.handleInternalServerError(e);
            return null;
        }
    }
}
