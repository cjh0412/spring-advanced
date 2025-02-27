package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.command.ChangePasswordCommand;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.example.expert.domain.common.exception.CommonErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void User를_ID로_조회할_수_있다() {
        // given
        String email = "asd@asd.com";
        long userId = 1L;
        User user = new User(email, "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponse userResponse = userService.getUser(userId);

        // then
        assertNotNull(userResponse);
        assertEquals(userResponse.getId(), userId);
        assertEquals(userResponse.getEmail(), email);
    }

    @Test
    void 존재하지_않는_User를_조회_시_InvalidRequestException을_던진다() {
        // Given
        long userId = 1L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidRequestException.class,
                () -> userService.getUser(userId), USER_NOT_FOUND.getMessage()
        );
    }

    @Test
    void 비밀번호변경중_존재하지_않는_User를_조회_시_InvalidRequestException을_던진다() {
        // Given
        long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        ChangePasswordCommand command = new ChangePasswordCommand(userId, oldPassword, newPassword);
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(command), USER_NOT_FOUND.getMessage()
        );
    }

    @Test
    void 비밀번호변경중_새비밀번호가_기존번호와_동일할_경우_InvalidRequestException을_던진다() {
        // Given
        long userId = 1L;
        String newPassword = "password";
        String oldPassword = "password";

        User user = new User("user1@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        ChangePasswordCommand command = new ChangePasswordCommand(userId, oldPassword, newPassword);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.getOldPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.matches(command.getNewPassword(), user.getPassword())).willReturn(true);

        // When & Then
        assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(command), PASSWORD_CANNOT_BE_SAME_AS_OLD.getMessage()
        );
    }

    @Test
    void 비밀번호변경중_잘못된_비밀번호를_입력한_경우_InvalidRequestException을_던진다() {
        // Given
        long userId = 1L;
        String newPassword = "password";
        String oldPassword = "oldPassword";

        User user = new User("user1@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        ChangePasswordCommand command = new ChangePasswordCommand(userId, oldPassword, newPassword);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.getOldPassword(), user.getPassword())).willReturn(false);

        // When & Then
        assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(command), WRONG_PASSWORD.getMessage()
        );
    }

    @Test
    void 비밀번호변경성공() {
        // Given
        long userId = 1L;
        String newPassword = "newPassword";
        String oldPassword = "oldPassword";

        User user = new User("user1@example.com", "oldPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        ChangePasswordCommand command = new ChangePasswordCommand(userId, oldPassword, newPassword);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.getOldPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.matches(command.getNewPassword(), user.getPassword())).willReturn(false);

        given(passwordEncoder.encode(command.getNewPassword())).willReturn("encodedPassword");

        // When
        userService.changePassword(command);

        //Then
        assertEquals("encodedPassword", user.getPassword());
    }

}