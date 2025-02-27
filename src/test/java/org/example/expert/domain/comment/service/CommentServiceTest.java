package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.command.CreateCommentCommand;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.CommonErrorCode;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
        // given
        long todoId = 1;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        CreateCommentCommand command = new CreateCommentCommand(authUser, todoId, "contents");

        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            commentService.saveComment(command);
        });

        // then
        assertEquals(CommonErrorCode.TODO_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void comment를_정상적으로_등록한다() {
        // given
        long todoId = 1;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        CreateCommentCommand command = new CreateCommentCommand(authUser, todoId, "contents");

        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "title", "contents", user);

        Comment comment = command.toDomain(user, todo);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(commentRepository.save(any())).willReturn(comment);

        // when
        CommentSaveResponse result = commentService.saveComment(command);

        // then
        assertNotNull(result);
    }

    @Test
    void 댓글_목록_조회(){
        //given
        long todoId = 1;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("title", "title", "contents", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        List<Comment> commentList = List.of(
                new Comment( "댓글1",  user, todo ),
                new Comment("댓글2",  user , todo )
        );

        given(commentRepository.findByTodoIdWithUser(todoId)).willReturn(commentList);

        // when
        List<CommentResponse> responses = commentService.getComments(todoId);

        // then
        assertEquals(2, responses.size());
        assertEquals(user.getId(), responses.get(0).getUser().getId());
        assertEquals("댓글2", responses.get(1).getContents());
        assertEquals(user.getId(), responses.get(1).getUser().getId());
    }

    @Test
    void 댓글_빈_목록_조회(){
        //given
        long todoId = 1;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("title", "title", "contents", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        given(commentRepository.findByTodoIdWithUser(todoId)).willReturn(Collections.emptyList());

        // when
        List<CommentResponse> responses = commentService.getComments(todoId);

        // then
        assertThat(responses).isEmpty();
    }

}
