package com.codecampus.identity.service.authentication;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codecampus.identity.dto.request.authentication.AuthenticationRequest;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.UserRepository;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceDiffblueTest {
  @InjectMocks
  private AuthenticationService authenticationService;

  @Mock
  private UserRepository userRepository;

  /**
   * Test {@link AuthenticationService#login(AuthenticationRequest)}.
   *
   * <p>Method under test: {@link AuthenticationService#login(AuthenticationRequest)}
   */
  @Test
  @DisplayName("Test login(AuthenticationRequest)")
  @Tag("ContributionFromDiffblue")
  @MethodsUnderTest({
      "com.codecampus.identity.dto.response.authentication.AuthenticationResponse AuthenticationService.login(AuthenticationRequest)"
  })
  void testLogin() throws ParseException {
    // Arrange
    when(userRepository.findByUsernameOrEmail(Mockito.any(),
        Mockito.any()))
        .thenThrow(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    AuthenticationRequest request = new AuthenticationRequest();
    request.setEmail("jane.doe@example.org");
    request.setPassword("iloveyou");
    request.setUsername("janedoe");

    // Act and Assert
    assertThrows(AppException.class,
        () -> authenticationService.login(request));
    verify(userRepository).findByUsernameOrEmail("janedoe",
        "jane.doe@example.org");
  }

  /**
   * Test {@link AuthenticationService#login(AuthenticationRequest)}.
   *
   * <ul>
   *   <li>Given {@link User#User()} Enabled is {@code false}.
   *   <li>Then throw {@link AppException}.
   * </ul>
   *
   * <p>Method under test: {@link AuthenticationService#login(AuthenticationRequest)}
   */
  @Test
  @DisplayName(
      "Test login(AuthenticationRequest); given User() Enabled is 'false'; then throw AppException")
  @Tag("ContributionFromDiffblue")
  @MethodsUnderTest({
      "com.codecampus.identity.dto.response.authentication.AuthenticationResponse AuthenticationService.login(AuthenticationRequest)"
  })
  void testLogin_givenUserEnabledIsFalse_thenThrowAppException()
      throws ParseException {
    // Arrange
    User user = new User();
    user.setCreatedAt(
        LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC)
            .toInstant());
    user.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
    user.setDeletedAt(
        LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC)
            .toInstant());
    user.setDeletedBy("Jan 1, 2020 11:00am GMT+0100");
    user.setEmail("jane.doe@example.org");
    user.setEnabled(false);
    user.setId("42");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setUpdatedAt(
        LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC)
            .toInstant());
    user.setUpdatedBy("2020-03-01");
    user.setUsername("janedoe");
    Optional<User> ofResult = Optional.of(user);
    when(userRepository.findByUsernameOrEmail(Mockito.any(),
        Mockito.any()))
        .thenReturn(ofResult);
    AuthenticationRequest request = new AuthenticationRequest();
    request.setEmail("jane.doe@example.org");
    request.setPassword("iloveyou");
    request.setUsername("janedoe");

    // Act and Assert
    assertThrows(AppException.class,
        () -> authenticationService.login(request));
    verify(userRepository).findByUsernameOrEmail("janedoe",
        "jane.doe@example.org");
  }

  /**
   * Test {@link AuthenticationService#login(AuthenticationRequest)}.
   *
   * <ul>
   *   <li>Given {@link User#User()} Enabled is {@code true}.
   *   <li>Then throw {@link AppException}.
   * </ul>
   *
   * <p>Method under test: {@link AuthenticationService#login(AuthenticationRequest)}
   */
  @Test
  @DisplayName(
      "Test login(AuthenticationRequest); given User() Enabled is 'true'; then throw AppException")
  @Tag("ContributionFromDiffblue")
  @MethodsUnderTest({
      "com.codecampus.identity.dto.response.authentication.AuthenticationResponse AuthenticationService.login(AuthenticationRequest)"
  })
  void testLogin_givenUserEnabledIsTrue_thenThrowAppException()
      throws ParseException {
    // Arrange
    User user = new User();
    user.setCreatedAt(
        LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC)
            .toInstant());
    user.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
    user.setDeletedAt(
        LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC)
            .toInstant());
    user.setDeletedBy("Jan 1, 2020 11:00am GMT+0100");
    user.setEmail("jane.doe@example.org");
    user.setEnabled(true);
    user.setId("42");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setUpdatedAt(
        LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC)
            .toInstant());
    user.setUpdatedBy("2020-03-01");
    user.setUsername("janedoe");
    Optional<User> ofResult = Optional.of(user);
    when(userRepository.findByUsernameOrEmail(Mockito.any(),
        Mockito.any()))
        .thenReturn(ofResult);
    AuthenticationRequest request = new AuthenticationRequest();
    request.setEmail("jane.doe@example.org");
    request.setPassword("iloveyou");
    request.setUsername("janedoe");

    // Act and Assert
    assertThrows(AppException.class,
        () -> authenticationService.login(request));
    verify(userRepository).findByUsernameOrEmail("janedoe",
        "jane.doe@example.org");
  }

  /**
   * Test {@link AuthenticationService#login(AuthenticationRequest)}.
   *
   * <ul>
   *   <li>Given {@link UserRepository} {@link UserRepository#findByUsernameOrEmail(String, String)}
   *       return empty.
   * </ul>
   *
   * <p>Method under test: {@link AuthenticationService#login(AuthenticationRequest)}
   */
  @Test
  @DisplayName(
      "Test login(AuthenticationRequest); given UserRepository findByUsernameOrEmail(String, String) return empty")
  @Tag("ContributionFromDiffblue")
  @MethodsUnderTest({
      "com.codecampus.identity.dto.response.authentication.AuthenticationResponse AuthenticationService.login(AuthenticationRequest)"
  })
  void testLogin_givenUserRepositoryFindByUsernameOrEmailReturnEmpty()
      throws ParseException {
    // Arrange
    Optional<User> emptyResult = Optional.empty();
    when(userRepository.findByUsernameOrEmail(Mockito.any(),
        Mockito.any()))
        .thenReturn(emptyResult);

    AuthenticationRequest request = new AuthenticationRequest();
    request.setEmail("jane.doe@example.org");
    request.setPassword("iloveyou");
    request.setUsername("janedoe");

    // Act and Assert
    assertThrows(AppException.class,
        () -> authenticationService.login(request));
    verify(userRepository).findByUsernameOrEmail("janedoe",
        "jane.doe@example.org");
  }
}
