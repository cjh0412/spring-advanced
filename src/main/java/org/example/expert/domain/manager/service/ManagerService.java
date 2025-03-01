package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
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
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static org.example.expert.domain.common.exception.CommonErrorCode.*;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public ManagerSaveResponse saveManager(CreateManagerCommand command) {
        // 일정을 만든 유저
        User user = User.fromAuthUser(command.getAuthUser());
        Todo todo = todoRepository.findById(command.getTodoId())
                .orElseThrow(() -> new InvalidRequestException(TODO_NOT_FOUND));

        if (todo.getUser() == null) {
            throw new InvalidRequestException(MANAGER_NOT_ALLOWED);
        }

        User managerUser = userRepository.findById(command.getManagerUserId())
                .orElseThrow(() -> new InvalidRequestException(MANAGER_NOT_FOUND));

        if (ObjectUtils.nullSafeEquals(user.getId(), managerUser.getId())) {
            throw new InvalidRequestException(TODO_CREATOR_CANNOT_BE_MANAGER);
        }

        Manager newManagerUser = command.toDomain(managerUser, todo);
        Manager savedManagerUser = managerRepository.save(newManagerUser);

        return  ManagerSaveResponse.toDto(savedManagerUser);
    }

    @Transactional(readOnly = true)
    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException(TODO_NOT_FOUND));

        return managerRepository.findByTodoIdWithUser(todo.getId())
                .stream()
                .map(ManagerResponse ::toDto)
                .toList();
    }

    @Transactional
    public void deleteManager(DeleteManagerCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new InvalidRequestException(USER_NOT_FOUND));

        Todo todo = todoRepository.findById(command.getTodoId())
                .orElseThrow(() -> new InvalidRequestException(TODO_NOT_FOUND));

        if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            throw new InvalidRequestException(INVALID_TODO_CREATOR);
        }

        Manager manager = managerRepository.findById(command.getManagerId())
                .orElseThrow(() -> new InvalidRequestException(MANAGER_NOT_FOUND));

        if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
            throw new InvalidRequestException(MANAGER_NOT_ASSIGNED);
        }

        managerRepository.delete(manager);
    }
}
