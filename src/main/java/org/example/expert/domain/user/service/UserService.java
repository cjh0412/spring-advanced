package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.command.ChangePasswordCommand;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.expert.domain.common.exception.CommonErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException(USER_NOT_FOUND));
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new InvalidRequestException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(command.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException(WRONG_PASSWORD);
        }

        if (passwordEncoder.matches(command.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException(PASSWORD_CANNOT_BE_SAME_AS_OLD);
        }

        user.changePassword(passwordEncoder.encode(command.getNewPassword()));
    }
}
