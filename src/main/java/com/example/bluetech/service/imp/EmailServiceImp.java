package com.example.bluetech.service.imp;

import com.example.bluetech.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendMailWithLink(String to, String subject, String templateName, String userName, Object value) {
        Context context = new Context();


        context.setVariable("link", value);
        context.setVariable("userName", userName);
        context.setVariable("email", to);
        String content = this.templateEngine.process(templateName, context);

        //send email
        this.sendEmailSync(to, subject, content, false, true);
    }

    @Async
    public void sendEmailSync(String to, String subject, String content, boolean isMultipart,
                              boolean isHtml) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
                    isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }
}
