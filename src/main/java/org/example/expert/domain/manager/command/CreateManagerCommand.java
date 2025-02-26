package org.example.expert.domain.manager.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class CreateManagerCommand {
    private AuthUser authUser;
    private Long todoId;
    private Long managerUserId;

    public Manager toDomain(User user, Todo todo){
        return new Manager(user, todo);
    }
}
