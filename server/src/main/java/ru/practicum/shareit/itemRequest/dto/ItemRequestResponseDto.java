package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;
}