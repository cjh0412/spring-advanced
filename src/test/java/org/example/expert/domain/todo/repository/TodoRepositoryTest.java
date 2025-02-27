package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void findAllByOrderByModifiedAtDesc() {
        //given
        String email = "asd@asd.com";
        User user = new User(email, "password", UserRole.USER);
        userRepository.save(user);

        Todo todo1 = new Todo("1Test Title", "Test Contents", "Sunny", user);
        todoRepository.save(todo1);
        todoRepository.flush();

        Todo todo2 = new Todo("2Test Title", "Test Contents", "Sunny", user);
        todoRepository.save(todo2);
        todoRepository.flush();

        Todo todo3 = new Todo("3Test Title", "Test Contents", "Sunny", user);
        todoRepository.save(todo3);
        todoRepository.flush();

        //when
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> todoPage = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        // then
        assertEquals(3, todoPage.getContent().size());
        assertEquals("1Test Title", todoPage.getContent().get(0).getTitle());

    }

    @Test
    void findByIdWithUser() {
        //given
        String email = "asd@asd.com";
        User user = new User(email, "password", UserRole.USER);
        userRepository.save(user);

        Todo todo = new Todo("1Test Title", "Test Contents", "Sunny", user);
        todoRepository.save(todo);

        //when
        Optional<Todo> result = todoRepository.findByIdWithUser(todo.getId());

        //then
        assertNotNull(result.get().getUser());
        assertEquals(email, result.get().getUser().getEmail());


    }
}