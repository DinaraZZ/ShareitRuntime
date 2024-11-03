package com.practice.shareitzeinolla.item.dto;

import com.practice.shareitzeinolla.item.Comment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CommentMapper {
    public Comment fromCommentCreate(CommentCreateDto commentCreateDto) {
        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setCommentDate(LocalDate.now());
        return comment;
    }

    public CommentResponseDto toResponse(Comment comment) {
        CommentResponseDto responseDto = new CommentResponseDto();
        responseDto.setId(comment.getId());
        responseDto.setAuthorName(comment.getUser().getName());
        responseDto.setItemId(comment.getItem().getId());
        responseDto.setText(comment.getText());
        responseDto.setCreated(comment.getCommentDate());
        return responseDto;
    }
}
