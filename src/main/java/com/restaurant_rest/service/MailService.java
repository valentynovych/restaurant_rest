package com.restaurant_rest.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MailService {
    private final SendGrid sendGrid;
    private final TemplateEngine templateEngine;
    @Value("${sendGrid.from}")
    private String mailFrom;
    private String confirmCode;

    @Async
    public CompletableFuture<String> sendEmailConfirmCode(String emailTo) {
        Email from = new Email(mailFrom);
        String subject = "Confirm Email";
        Email to = new Email(emailTo);
        Content content = new Content("text/html", buildMail(emailTo));
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
//            Response response = sendGrid.api(request);
//            System.out.println(response.getStatusCode());
//            System.out.println(response.getBody());
//            System.out.println(response.getHeaders());
            return CompletableFuture.completedFuture(confirmCode);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }

    private String buildMail(String emailTo) {
        Random random = new Random();
        Context context = new Context();
        confirmCode = String.valueOf(random.nextInt(1000, 9000));
        context.setVariable("email", emailTo);
        context.setVariable("confirmCode", confirmCode);
        return templateEngine.process("confirmEmail", context);
    }

}
