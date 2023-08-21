package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class DbItemStorageImpl implements ItemStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Item> getItems(Long userId) {
        final String sqlQuery = "SELECT * " +
                "FROM item " +
                "WHERE owner = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRow, userId);
    }

    @Override
    public Item getItem(Long itemId) {
        final String sqlQuery = "SELECT * " +
                "FROM item " +
                "WHERE id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRow, itemId);
    }

    @Override
    public Item addItem(Long userId, Item item) {
        final String sqlQuery = "INSERT INTO item (name, description, available, owner) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, item.getName());
            statement.setString(2, item.getDescription());
            statement.setBoolean(3, item.getAvailable());
            statement.setLong(4, userId);
            return statement;
        }, keyHolder);

        item.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item updatedItem = getItem(itemId);

        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("User don't have access to this item.");
        }

        ItemValidator.itemPatch(updatedItem, item);

        final String sqlQuery = "UPDATE item SET name = ?, description = ?, available = ? " +
                "WHERE id = ? AND OWNER = ?";
        jdbcTemplate.update(sqlQuery,
                updatedItem.getName(),
                updatedItem.getDescription(),
                updatedItem.getAvailable(),
                updatedItem.getId(),
                userId);
        return updatedItem;
    }

    @Override
    public Boolean deleteItem(Long itemId) {
        final String sqlQuery = "DELETE FROM item WHERE id = ?";

        return jdbcTemplate.update(sqlQuery, itemId) > 0;
    }

    @Override
    public Boolean isItemExists(Long id) {
        final String sqlQuery = "SELECT EXISTS(SELECT * " +
                "FROM item " +
                "WHERE id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id));
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        String search = '%' + text.toLowerCase() + '%';
        final String sqlQuery = "SELECT * FROM item WHERE " +
                "(LOWER(name) LIKE ? OR LOWER(description) LIKE ?) " +
                "AND available = true";

        return jdbcTemplate.query(sqlQuery, this::mapRow, search, search);
    }

    private Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        long ownerId = rs.getLong("owner");
        User owner = getUser(ownerId);

        return Item.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .available(rs.getBoolean("available"))
                .owner(owner)
                .build();
    }

    private User getUser(Long id) {
        final String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE id = ? ";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowUser, id);
    }

    private User mapRowUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build();
    }
}