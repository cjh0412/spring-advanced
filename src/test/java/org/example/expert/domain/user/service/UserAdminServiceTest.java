package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.command.ChangeUserRoleCommand;
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

import static org.example.expert.domain.common.exception.CommonErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void 존재하지_않는_User를_조회_시_InvalidRequestException을_던진다() {
        // Given
        long userId = 1L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        ChangeUserRoleCommand command = new ChangeUserRoleCommand(userId, "ADMIN");

        // When & Then
        assertThrows(InvalidRequestException.class,
                () -> userAdminService.changeUserRole(command), USER_NOT_FOUND.getMessage()
        );
    }

    @Test
    void userRole를_변경한다() {
        // Given
        long userId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);
        ChangeUserRoleCommand command = new ChangeUserRoleCommand(userId, "ADMIN");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // When
        userAdminService.changeUserRole(command);

        //Then
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }

}