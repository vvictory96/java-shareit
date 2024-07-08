package ru.practicum.shareit.comment.service;


import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto add(long itemId, CommentDto comment, long userId);

    List<CommentDto> getAllCommentsByItemId(long itemId);
}
