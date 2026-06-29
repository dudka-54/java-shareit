package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item mapToItem(CreateItemRequest request) {
        if (request == null) {
            return null;
        }
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .request(ItemRequestMapper.toDto(item.getRequest()))
                .build();
    }

    public static ItemShortDto toItemShortDto(Item item) {
        if (item == null) {
            return null;
        }

        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    public static ItemBookingDto toItemBookingDto(Item item,
                                                  Booking lastBooking,
                                                  Booking nextBooking) {
        if (item == null) {
            return null;
        }

        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingInfoDto(lastBooking))
                .nextBooking(BookingMapper.toBookingInfoDto(nextBooking))
                .build();
    }

    public static ItemBookingCommentDto toItemBookingCommentDto(Item item,
                                                                Booking lastBooking,
                                                                Booking nextBooking) {
        if (item == null) {
            return null;
        }

        return ItemBookingCommentDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingInfoDto(lastBooking))
                .nextBooking(BookingMapper.toBookingInfoDto(nextBooking))
                .comments(mapComments(item.getComments()))
                .build();
    }

    private static List<CommentDto> mapComments(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
