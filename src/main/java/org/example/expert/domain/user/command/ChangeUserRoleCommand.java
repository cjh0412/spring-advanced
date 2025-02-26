package org.example.expert.domain.user.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangeUserRoleCommand {
    private Long userId;
    private String role;
}
