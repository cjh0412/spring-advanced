package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.command.CreateTodoCommand;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.common.exception.CommonErrorCode.TODO_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    void 할일_저장_성공(){
        //given
        String title ="제목";
        String contents = "할일";
        String weather = "sunny";

        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        CreateTodoCommand command = new CreateTodoCommand(authUser, title, contents);

        User user = User.fromAuthUser(command.getAuthUser());
        Todo todo = command.toDomain(weather, user);

        given(todoRepository.save(any(Todo.class))).willReturn(todo);
        given(weatherClient.getTodayWeather()).willReturn(weather);

        // when
        TodoSaveResponse response = todoService.saveTodo(command);

        //then
        assertNotNull(response);
        assertEquals("제목", response.getTitle());

        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(weatherClient, times(1)).getTodayWeather();

    }

    @Test
    void todo_목록을_조회한다(){
        //given
        String title ="제목";
        String contents = "할일";
        String weather = "sunny";
        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page-1, size);

        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo(title, contents, weather, user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        List<Todo> todoList = List.of(
                new Todo(title, contents, weather, user),
                new Todo(title, contents, weather, user)
        );

        Page<Todo> todoPage = new PageImpl<>(todoList, pageable, todoList.size());

        // when
        given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todoPage);
        Page<TodoResponse> responses = todoService.getTodos(page, size);

        assertNotNull(responses);
        assertEquals(2, responses.getContent().size());
        assertEquals("제목", responses.getContent().get(0).getTitle());

    }

    @Test
    void todo_조회시_todo가_없는경우_예외가_발생한다(){
        //given
        long todoId = 1L;
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.empty());

        // when
        assertThrows(InvalidRequestException.class,
                () -> todoService.getTodo(todoId), TODO_NOT_FOUND.getMessage());
    }

    @Test
    void todo_단건_조회_성공(){
        //given
        String title ="제목";
        String contents = "할일";
        String weather = "sunny";
        long todoId = 1L;

        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo(title, contents, weather, user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(todoId);

        //then
        assertNotNull(response);
        assertEquals(title,response.getTitle());
    }


}