package com.homeservices.service;

import com.homeservices.entity.AppUser;
import com.homeservices.entity.ProviderProfile;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.enums.Role;
import com.homeservices.repository.AppUserRepository;
import com.homeservices.repository.ProviderProfileRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class RegistrationService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+(). -]{7,20}$");

    private final AppUserRepository users;
    private final ProviderProfileRepository providers;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(AppUserRepository users, ProviderProfileRepository providers, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.providers = providers;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerCustomer(String fullName, String email, String phone, String rawPassword) {
        registerBase(fullName, email, phone, rawPassword, Role.CUSTOMER);
    }

    @Transactional
    public void registerProvider(String fullName, String email, String phone, String rawPassword,
                                 String businessName, String description, Integer experienceYears,
                                 String city, String area, String address) {
        validateProviderDetails(businessName, description, experienceYears, city, area, address);
        AppUser user = registerBase(fullName, email, phone, rawPassword, Role.PROVIDER);
        ProviderProfile profile = new ProviderProfile();
        profile.setUser(user);
        profile.setBusinessName(businessName.trim());
        profile.setDescription(blankToNull(description));
        profile.setExperienceYears(experienceYears == null ? 0 : experienceYears);
        profile.setCity(city.trim());
        profile.setArea(blankToNull(area));
        profile.setAddress(blankToNull(address));
        profile.setStatus(ProviderStatus.PENDING);
        providers.save(profile);
    }

    private AppUser registerBase(String fullName, String email, String phone, String rawPassword, Role role) {
        validateCommonDetails(fullName, email, phone, rawPassword);
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        AppUser user = new AppUser();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPhone(blankToNull(phone));
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        try {
            // Flush here so the unique email constraint is translated to a friendly
            // validation message rather than surfacing as a later generic SQL error.
            return users.saveAndFlush(user);
        } catch (DataIntegrityViolationException duplicate) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
    }

    private void validateCommonDetails(String fullName, String email, String phone, String rawPassword) {
        if (isBlank(fullName) || fullName.trim().length() > 100) {
            throw new IllegalArgumentException("Full name is required and must be 100 characters or fewer.");
        }
        if (isBlank(email) || email.trim().length() > 150 || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid email address (maximum 150 characters).");
        }
        if (!isBlank(phone) && !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid phone number using 7–20 digits or phone symbols.");
        }
        if (rawPassword == null || rawPassword.length() < 8
                || rawPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must be at least 8 characters and no more than 72 UTF-8 bytes.");
        }
    }

    private void validateProviderDetails(String businessName, String description, Integer experienceYears,
                                         String city, String area, String address) {
        if (isBlank(businessName) || businessName.trim().length() > 120) {
            throw new IllegalArgumentException("Business name is required and must be 120 characters or fewer.");
        }
        if (isBlank(city) || city.trim().length() > 80) {
            throw new IllegalArgumentException("City is required and must be 80 characters or fewer.");
        }
        if (experienceYears != null && (experienceYears < 0 || experienceYears > 60)) {
            throw new IllegalArgumentException("Experience must be between 0 and 60 years.");
        }
        if (description != null && description.length() > 4000) {
            throw new IllegalArgumentException("Business description must be 4,000 characters or fewer.");
        }
        if (area != null && area.length() > 100) {
            throw new IllegalArgumentException("Area must be 100 characters or fewer.");
        }
        if (address != null && address.length() > 255) {
            throw new IllegalArgumentException("Address must be 255 characters or fewer.");
        }
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
    private String blankToNull(String value) { return isBlank(value) ? null : value.trim(); }
}
