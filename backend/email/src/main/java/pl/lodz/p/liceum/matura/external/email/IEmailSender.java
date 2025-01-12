package pl.lodz.p.liceum.matura.external.email;

public interface IEmailSender {
    void send(SendEmailCommand command);
}
