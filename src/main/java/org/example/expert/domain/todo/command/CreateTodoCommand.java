package org.example.expert.domain.todo.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class CreateTodoCommand {
    private AuthUser authUser;
    private String title;
    private String contents;

    public Todo toDomain(String weather, User user){
        return new Todo(this.title, this.contents, weather, user);
    }
}
