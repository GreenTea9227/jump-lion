package springboot.jump.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@PropertySource(value = "classpath:formsg.properties")
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${FROM_ADDRESS}")
    private String FROM_ADDRESS;

    public void sendMail(String email,String uuid) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(FROM_ADDRESS);
        message.setSubject("localhost:8080 email 확인");
        message.setText(uuid);

        mailSender.send(message);


    }
}
