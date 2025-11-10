package com.khouss.UsersMicroservice;

import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.events.UserCreatedEvent;
import com.khouss.UsersMicroservice.repo.ClientRepository;
import com.khouss.UsersMicroservice.services.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserToCompteEventIntegrationTest {

    @Autowired
    private EventPublisherComponent publisher;

    @MockBean
    private SmsService smsService;

    @Test
    void quand_un_user_created_est_publie_alors_le_compte_est_cree_et_un_sms_est_envoye() {
        User user = new User();
        user.setId(java.util.UUID.randomUUID());
        user.setUsername("testuser");
        user.setTelephone("+221774730040");

        publisher.publish(user);


        verify(smsService, timeout(3000)).sendSMS(anyString(), anyString());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public EventPublisherComponent eventPublisherComponent(ApplicationEventPublisher publisher) {
            return new EventPublisherComponent(publisher);
        }
    }

    @Component
    public static class EventPublisherComponent {
        private final ApplicationEventPublisher publisher;

        public EventPublisherComponent(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Transactional
        public void publish(User user) {
            publisher.publishEvent(new UserCreatedEvent(this, user));
        }
    }
}

