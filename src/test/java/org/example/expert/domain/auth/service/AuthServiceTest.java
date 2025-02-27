package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
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

import static org.example.expert.domain.auth.exception.AuthErrorCode.WRONG_PASSWORD;
import static org.example.expert.domain.common.exception.CommonErrorCode.EMAIL_ALREADY_EXISTS;
import static org.example.expert.domain.common.exception.CommonErrorCode.UNREGISTERED_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입_성공(){

        //given
        String email = "test@test.com";
        String password = "password";
        String encodePassword = passwordEncoder.encode(password);
        UserRole userRole = UserRole.USER;
        String token = "mocked-jwt-token";

        SignupRequest request = new SignupRequest(email, "password", "USER");

        // 이메일 존재 여부 체크
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // 비밀번호 인코딩
        given(passwordEncoder.encode(password)).willReturn(encodePassword);
        User testUser = new User(email, encodePassword, userRole);
        ReflectionTestUtils.setField(testUser, "id", 1L);

        given(userRepository.save(any(User.class))).willReturn(testUser);

        // jwt 토큰 생성
        given(jwtUtil.createToken(testUser.getId(), testUser.getEmail(), testUser.getUserRole())).willReturn(token);

        //when
        SignupResponse response = authService.signup(request);

        //then
        assertNotNull(response);
        assertEquals(token, response.getBearerToken());

    }

    @Test
    void 회원가입시_이미_존재하는_email인_경우_예외처리한다(){
        // given
        String email = "test@test.com";
        User testUser = new User(email, "password", UserRole.USER);
        SignupRequest request = new SignupRequest(email, "password", "USER");
        ReflectionTestUtils.setField(testUser, "id", 1L);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signup(request);
        });

        // then
        assertEquals(exception.getMessage(), EMAIL_ALREADY_EXISTS.getMessage());

    }

    @Test
    void 로그인_성공(){

        //given
        String email = "test@test.com";
        String password = "password";
        String encodePassword = passwordEncoder.encode(password);
        UserRole userRole = UserRole.USER;
        String token = "mocked-jwt-token";

        User testUser = new User(email, encodePassword, userRole);
        ReflectionTestUtils.setField(testUser, "id", 1L);

        SigninRequest request = new SigninRequest(email, password);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).willReturn( true);

        // when
        given(jwtUtil.createToken(testUser.getId(), testUser.getEmail(), testUser.getUserRole())).willReturn(token);

        //when
        SigninResponse response = authService.signin(request);

        //then
        assertNotNull(response);
        assertEquals(token, response.getBearerToken());

    }


    @Test
    void 로그인시_존재하지_않는_email인_경우_예외처리한다(){
        // given
        String email = "test@test.com";
        String password = "password";
        UserRole userRole = UserRole.USER;
        User testUser = new User(email, password, userRole);
        SigninRequest request = new SigninRequest(email, password);
        ReflectionTestUtils.setField(testUser, "id", 1L);

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signin(request);
        });

        // then
        assertEquals(exception.getMessage(), UNREGISTERED_USER.getMessage());
    }

    @Test
    void 로그인시_비밀번호가_일치하지_않는_경우_예외처리한다(){
        // given
        String email = "test@test.com";
        User testUser = new User(email, passwordEncoder.encode("password"), UserRole.USER);
        SigninRequest request = new SigninRequest(email, "password");
        ReflectionTestUtils.setField(testUser, "id", 1L);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).willReturn(false);

        // when
        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.signin(request);
        });

        // then
        assertEquals(exception.getMessage(), WRONG_PASSWORD.getMessage());
    }
}