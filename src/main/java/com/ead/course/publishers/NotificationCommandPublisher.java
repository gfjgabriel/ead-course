package com.ead.course.publishers;

import com.ead.course.dtos.NotificationCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${ead.broker.exchange.notificationCommandExchange}")
    private String exchangeNotificationCommand;

    @Value(value = "${ead.broker.key.notificationCommandKey}")
    private String notificationCommandKey;

    public NotificationCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishNotificationCommand(NotificationCommandDTO notificationCommand) {
        rabbitTemplate.convertAndSend(exchangeNotificationCommand, notificationCommandKey, notificationCommand);
    }
}
