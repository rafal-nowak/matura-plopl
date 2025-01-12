package pl.lodz.p.liceum.matura.external.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.appservices.Email;
import pl.lodz.p.liceum.matura.appservices.IEmail;

@RequiredArgsConstructor
@Log
@Service
public class EmailAdapter implements IEmail {

    private final KafkaSendEmailCommand kafkaSendEmailCommand;

    @Override
    public void send(final Email email) {
        kafkaSendEmailCommand.send(
                new SendEmailCommand(
                        email.getSubject(),
                        email.getTo(),
                        email.getCc(),
                        email.getBcc(),
                        email.getBody()
                )
        );
    }
}
