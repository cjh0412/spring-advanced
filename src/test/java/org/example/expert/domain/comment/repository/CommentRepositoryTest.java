package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByTodoIdWithUser() {
        //given
        String email = "asd@asd.com";
        User user = new User(email, "password", UserRole.USER);
        userRepository.save(user);

        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);
        todoRepository.save(todo);

        Comment comment = new Comment("댓글내용", user, todo);
        commentRepository.save(comment);

        //when
        List<Comment> comments = commentRepository.findByTodoIdWithUser(todo.getId());

        //then
        assertFalse(comments.isEmpty());
        assertEquals(user.getEmail(), comments.get(0).getUser().getEmail());
        assertEquals(todo.getId(), comments.get(0).getTodo().getId());
        assertEquals(comment.getContents(), comments.get(0).getContents());
    }

}