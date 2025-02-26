package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.command.CreateTodoCommand;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.expert.domain.common.exception.CommonErrorCode.TODO_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    @Transactional
    public TodoSaveResponse saveTodo(CreateTodoCommand command) {
        User user = User.fromAuthUser(command.getAuthUser());
        String weather = weatherClient.getTodayWeather();

        Todo newTodo = command.toDomain(weather, user);
        Todo savedTodo = todoRepository.save(newTodo);

        return TodoSaveResponse.toDto(savedTodo);
    }

    public Page<TodoResponse> getTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        return todos.map(TodoResponse::toDto);
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException(TODO_NOT_FOUND));

        return TodoResponse.toDto(todo);
    }
}
