package org.example.expert.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.command.CreateCommentCommand;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.exception.CommonErrorCode;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentSaveResponse saveComment(CreateCommentCommand command) {
        User user = User.fromAuthUser(command.getAuthUser());
        Todo todo = todoRepository.findById(command.getTodoId())
                .orElseThrow(() -> new InvalidRequestException(CommonErrorCode.TODO_NOT_FOUND));

        Comment newComment = command.toDomain(user, todo);
        Comment savedComment = commentRepository.save(newComment);

        return CommentSaveResponse.toDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(long todoId) {
        return commentRepository.findByTodoIdWithUser(todoId)
                .stream()
                .map(CommentResponse :: toDto)
                .toList();
    }
}
