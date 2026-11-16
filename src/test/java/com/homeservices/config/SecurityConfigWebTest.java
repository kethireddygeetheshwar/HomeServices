package com.homeservices.config;

import com.homeservices.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProbeController.class)
@Import(SecurityConfig.class)
class SecurityConfigWebTest {
    @Autowired MockMvc mvc;
    @MockBean CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotOpenAdminOrProviderPages() throws Exception {
        mvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/access-denied"));
        mvc.perform(get("/provider/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/access-denied"));
    }

    @Test
    @WithMockUser(roles = "PROVIDER")
    void providerCannotOpenAdminApi() throws Exception {
        mvc.perform(get("/api/v1/admin/probe"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedApiRequestReturns401() throws Exception {
        mvc.perform(get("/api/v1/admin/probe"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCanAccessCustomerApiWhenCsrfTokenIsPresent() throws Exception {
        mvc.perform(post("/api/v1/bookings/probe")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

}
