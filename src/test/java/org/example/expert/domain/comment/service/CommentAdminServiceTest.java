package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    void 관리자_댓글_삭제_성공(){
        //given
        long commentId = 1L;
        String contents = "댓글내용입니다.";
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);

        Comment comment = new Comment(contents, user, todo);
        ReflectionTestUtils.setField(comment, "id", commentId);

        // when
        commentAdminService.deleteComment(commentId);

        //then
        verify(commentRepository, times(1)).deleteById(commentId);
    }
}