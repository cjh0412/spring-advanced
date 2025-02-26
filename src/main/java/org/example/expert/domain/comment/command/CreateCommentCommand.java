package org.example.expert.domain.comment.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class CreateCommentCommand {
    private AuthUser authUser;
    private Long todoId;
    private String contents;

    public Comment toDomain(User user, Todo todo){
        return new Comment(this.contents, user, todo);
    }

}
