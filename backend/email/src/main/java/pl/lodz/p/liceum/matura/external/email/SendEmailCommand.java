package pl.lodz.p.liceum.matura.external.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailCommand {

    String subject;
    List<String> to;
    List<String> cc;
    List<String> bcc;
    String body;

}

