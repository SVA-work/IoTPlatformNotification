package application.kafka;

import application.dto.NotificationDto;
import application.telegrambot.bot.IoTServiceBot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

  private final ObjectMapper objectMapper;
  private final IoTServiceBot ioTServiceBot;

  @KafkaListener(topics = "${topic-to-consume-message}")
  public void consumeMessage(String message, Acknowledgment acknowledgment) {
    try {
      KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);

      switch (kafkaMessage.getCommand()) {
        case HIGH_TEMPERATURE -> {
          NotificationDto notificationDto = objectMapper.readValue(kafkaMessage.getMessage(), NotificationDto.class);
          ioTServiceBot.sendHighTempNotification(notificationDto);
        }
        case LOW_TEMPERATURE -> {
          NotificationDto notificationDto = objectMapper.readValue(kafkaMessage.getMessage(), NotificationDto.class);
          ioTServiceBot.sendLowerTempNotification(notificationDto);
        }
        default -> LOGGER.error("Not valid command in request");
      }


      acknowledgment.acknowledge();
    } catch (JsonProcessingException e) {
      LOGGER.error("Error processing Kafka message: {}", message, e);
    } catch (Exception e) {
      LOGGER.error("Unexpected error while processing audit message", e);
    }
  }
}