package com.weShare.api.v1.auth;

import com.weShare.api.v1.IntegrationMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends IntegrationMvcTestSupport {

    @Test
    @DisplayName("사용자는 로그인을 할 수 있다.")
    public void signup() throws Exception {
        // given
        SignupRequest request = createSignupRequest("email@asd.com", "password", LocalDate.of(1999, 9, 27));
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("test"))
                .andDo(print());
        // when

        // then
    }

    private SignupRequest createSignupRequest(String email, String password, LocalDate birthDate) {
        return SignupRequest.builder()
                .email(email)
                .password(password)
                .birthDate(birthDate)
                .build();
    }
}