package org.example.expert.domain.manager.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.command.CreateManagerCommand;
import org.example.expert.domain.manager.command.DeleteManagerCommand;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.common.exception.CommonErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Test
    public void todo_목록_조회_시_Todo가_없다면_IRE_에러를_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
        assertEquals(TODO_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "user", null);

        CreateManagerCommand command = new CreateManagerCommand(authUser, todoId, managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(command)
        );

        // then
        assertEquals(MANAGER_NOT_ALLOWED.getMessage(), exception.getMessage());
    }

    @Test
    void user와_managerUser가_동일할_경우_예외를_발생한다(){
        // given
        long todoId = 1L;
        long managerUserId = 2L;
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);

        CreateManagerCommand command = new CreateManagerCommand(authUser, todoId, managerUserId);
        User user = User.fromAuthUser(command.getAuthUser());

        Todo todo = new Todo("title", "title", "contents", user);
        ReflectionTestUtils.setField(todo, "id",  todoId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(command.getManagerUserId())).willReturn(Optional.of(user));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(command)
        );

        // then
        assertEquals(TODO_CREATOR_CANNOT_BE_MANAGER.getMessage(), exception.getMessage());
    }

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        Manager mockManager = new Manager(todo.getUser(), todo);
        List<Manager> managerList = List.of(mockManager);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManager.getId(), managerResponses.get(0).getId());
        assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }

    @Test // 테스트코드 샘플
    void todo가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        CreateManagerCommand command = new CreateManagerCommand(authUser, todoId, managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(command);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    void 삭제_user목록을_조회시_없다면_예외를_발생한다(){
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;

        DeleteManagerCommand command = new DeleteManagerCommand(userId, todoId, managerId);

        given(userRepository.findById(command.getUserId())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(command)
        );

        // then
        assertEquals(USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    public void 삭제_todo_목록_조회_시_Todo가_없다면_예외를_발생한다() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;

        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(user, "id", userId);

        DeleteManagerCommand command = new DeleteManagerCommand(userId, todoId, managerId);

        given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when
        InvalidRequestException exception =
                assertThrows(InvalidRequestException.class, () -> managerService.deleteManager(command));

        //then
        assertEquals(TODO_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 삭제_todo의_user가_null인_경우_예외가_발생한다() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;

        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "id", null);
        ReflectionTestUtils.setField(user, "id", userId);

        DeleteManagerCommand command = new DeleteManagerCommand(userId, todoId, managerId);

        given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(command)
        );

        // then
        assertEquals(INVALID_TODO_CREATOR.getMessage(), exception.getMessage());
    }

    @Test
    void 삭제_manager_목록_조회시_없다면_예외가_발생한다() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;

        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);

        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(user, "id", userId);

        DeleteManagerCommand command = new DeleteManagerCommand(userId, todoId, managerId);

        given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findById(command.getManagerId())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(command)
        );

        // then
        assertEquals(MANAGER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 삭제_todo의_담당자가_아니라면_예외가_발생한다() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;

        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        Todo wrongTodo = new Todo("Title", "Contents", "Sunny", user);

        ReflectionTestUtils.setField(todo, "id", 2L);
        ReflectionTestUtils.setField(user, "id", userId);

        Manager manager = new Manager(user, todo);
        ReflectionTestUtils.setField(manager, "id", managerId);

        System.out.println("Todo ID from Repository: " + todo.getId());  // 2L
        System.out.println("Manager's Todo ID: " + manager.getTodo().getId());  // 1L

        DeleteManagerCommand command = new DeleteManagerCommand(userId, todoId, managerId);

        given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(wrongTodo));
        given(managerRepository.findById(command.getManagerId())).willReturn(Optional.of(manager));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(command)
        );

        // then
        assertEquals(MANAGER_NOT_ASSIGNED.getMessage(), exception.getMessage());
    }

    @Test
    void 삭제_성공() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerId = 1L;

        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        Manager manager = new Manager(user, todo);

        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(manager, "id", managerId);

        DeleteManagerCommand command = new DeleteManagerCommand(userId, todoId, managerId);

        given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findById(command.getManagerId())).willReturn(Optional.of(manager));

        // when
        managerService.deleteManager(command);

        // then
        verify(managerRepository, times(1)).delete(manager);
    }

}
