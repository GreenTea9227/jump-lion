package springboot.jump.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    private String FROM_ADDRESS = "MY_ADDRESS";

    public void sendMail(String email, String uuid) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(FROM_ADDRESS);
        message.setSubject("localhost:8080 email 확인");
        message.setText(uuid);

        mailSender.send(message);
    }
}
