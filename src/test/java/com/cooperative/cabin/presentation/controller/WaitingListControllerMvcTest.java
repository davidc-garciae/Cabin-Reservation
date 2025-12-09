package com.cooperative.cabin.presentation.controller;

import com.cooperative.cabin.TestMvcConfiguration;
import com.cooperative.cabin.application.service.WaitingListApplicationService;
import com.cooperative.cabin.infrastructure.security.JwtService;
import com.cooperative.cabin.presentation.dto.waitinglist.ClaimRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaitingListController.class)
@WithMockUser
@ActiveProfiles("test")
@Import(TestMvcConfiguration.class)
class WaitingListControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WaitingListApplicationService waitingListApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnOkWhenClaimSucceeds() throws Exception {
        WaitingListApplicationService.ClaimResult result = new WaitingListApplicationService.ClaimResult(
                1L, 2L, 3L, 4L);
        when(waitingListApplicationService.claim(any())).thenReturn(Optional.of(result));

        ClaimRequest req = new ClaimRequest("token", 2);

        mockMvc.perform(post("/api/waiting-list/claim")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn410WhenClaimFails() throws Exception {
        when(waitingListApplicationService.claim(any())).thenReturn(Optional.empty());

        ClaimRequest req = new ClaimRequest("token", 2);

        mockMvc.perform(post("/api/waiting-list/claim")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isGone());
    }
}

