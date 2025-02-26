package org.example.expert.domain.manager.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteManagerCommand {
    private Long userId;
    private Long todoId;
    private Long managerId;
}
