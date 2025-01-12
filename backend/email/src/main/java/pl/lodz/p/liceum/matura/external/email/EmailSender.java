package pl.lodz.p.liceum.matura.external.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class EmailSender implements IEmailSender {
    private final JavaMailSender mailSender;

    @Override
    public void send(final SendEmailCommand command) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(command.getTo().get(0));
        message.setSubject(command.getSubject());
        message.setText(command.getBody());
        mailSender.send(message);
    }
}
