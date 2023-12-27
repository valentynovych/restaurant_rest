package com.restaurant_rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private SendGrid sendGrid;

    @Mock
    private TemplateEngine templateEngine;

    @Value("${sendGrid.from}")
    private String mailFrom;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
        mailService = new MailService(sendGrid, templateEngine);
    }

    @Test
    void testSendEmailConfirmCode() throws ExecutionException, InterruptedException, IOException {
        ReflectionTestUtils.setField(mailService, "mailFrom", mailFrom);
        String emailTo = "test@example.com";
        String confirmCode = "1234";

        when(templateEngine.process(eq("confirmEmail"), any())).thenReturn("Email body with confirmation code: " + confirmCode);
        Map<String, String> hearer = new HashMap<>();
        hearer.put("Status", "Ok");
        when(sendGrid.api(any(Request.class))).thenReturn(new Response(200, "OK", hearer));

        CompletableFuture<String> result = mailService.sendEmailConfirmCode(emailTo);
        assertNotNull(result);
        result.join();
        verify(sendGrid, times(1)).api(any(Request.class));
        assertEquals(4, result.get().length());
    }

    @Test
    void testSendEmailConfirmCode_ifBuildThrowException() throws IOException {
        ReflectionTestUtils.setField(mailService, "mailFrom", null);
        String confirmCode = "1234";

        when(templateEngine.process(eq("confirmEmail"), any())).thenReturn("Email body with confirmation code: " + confirmCode);
        when(sendGrid.api(any(Request.class))).thenThrow(new IOException());

        assertThrows(RuntimeException.class, () -> mailService.sendEmailConfirmCode(null));
    }
}