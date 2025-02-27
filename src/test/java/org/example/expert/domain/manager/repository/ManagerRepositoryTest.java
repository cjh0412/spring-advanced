package org.example.expert.domain.manager.repository;

import jakarta.persistence.EntityManager;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ManagerRepositoryTest {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void findByTodoIdWithUser() {
        //given
        String email = "asd@asd.com";
        User user = new User(email, "password", UserRole.USER);
        userRepository.save(user);

        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);
        todoRepository.save(todo);

        Manager manager = new Manager(user, todo);
        managerRepository.save(manager);

        // when
        List<Manager> managers = managerRepository.findByTodoIdWithUser(todo.getId());

        // then
        assertFalse(managers.isEmpty());
        assertEquals(user.getEmail(), managers.get(0).getUser().getEmail());
        assertEquals(todo.getId(), managers.get(0).getTodo().getId());
    }
}