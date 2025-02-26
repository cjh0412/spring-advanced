package org.example.expert.domain.user.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordCommand {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
