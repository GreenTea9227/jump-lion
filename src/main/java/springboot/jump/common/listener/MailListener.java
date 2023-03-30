package springboot.jump.common.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import springboot.jump.aggregate.user.EmailEvent;
import springboot.jump.common.util.MailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailListener {

    private final MailService mailService;

    @Async("forEmailService")
    @EventListener
    public void sendEmail(EmailEvent event) {
        log.info("현재 쓰레드(비동기 확인 로그) : {}", Thread.currentThread().getId());
        mailService.sendMail(event.getEmail(), event.getUuid());
    }
}
