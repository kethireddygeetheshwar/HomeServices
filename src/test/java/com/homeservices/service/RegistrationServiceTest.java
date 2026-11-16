package com.homeservices.service;

import com.homeservices.entity.AppUser;
import com.homeservices.enums.Role;
import com.homeservices.repository.AppUserRepository;
import com.homeservices.repository.ProviderProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock AppUserRepository users;
    @Mock ProviderProfileRepository providers;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks RegistrationService service;

    @Test
    void customerEmailIsNormalizedAndPasswordIsEncoded() {
        when(users.existsByEmailIgnoreCase("person@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPass123")).thenReturn("$2a$encoded-value");
        when(users.saveAndFlush(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.registerCustomer(" Test Person ", " PERSON@EXAMPLE.COM ", "9876543210", "StrongPass123");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(users).saveAndFlush(captor.capture());
        AppUser saved = captor.getValue();
        assertEquals("Test Person", saved.getFullName());
        assertEquals("person@example.com", saved.getEmail());
        assertEquals("$2a$encoded-value", saved.getPassword());
        assertEquals(Role.CUSTOMER, saved.getRole());
        verify(passwordEncoder).encode("StrongPass123");
    }

    @Test
    void rejectsInvalidEmailBeforePersistence() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer("Person", "not-an-email", null, "StrongPass123"));
        verify(users, never()).saveAndFlush(any());
    }

    @Test
    void rejectsPasswordThatExceedsBcryptByteLimit() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer("Person", "person@example.com", null, "x".repeat(73)));
        verify(users, never()).saveAndFlush(any());
    }

    @Test
    void rejectsDuplicateEmail() {
        when(users.existsByEmailIgnoreCase("person@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer("Person", "person@example.com", null, "StrongPass123"));
        verify(users, never()).saveAndFlush(any());
    }
}
