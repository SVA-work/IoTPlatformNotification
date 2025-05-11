package application.telegrambot.bot;

import application.dto.NotificationDto;
import application.telegrambot.devicenotification.TemperatureNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class IoTServiceBot extends TelegramLongPollingBot {

    private static final String START = "/start";

    private static final TemperatureNotification TEMPERATURE_NOTIFICATION = new TemperatureNotification();

    public IoTServiceBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        if (START.equals(message)) {
            String userName = update.getMessage().getChat().getUserName();
            startCommand(chatId, userName);
        }
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
            Добро пожаловать в бот, %s!
            Иcпользуйте этот телеграм токен при регистрации: %d
            """;
        var formattedText = String.format(text, userName, chatId);
        String chatIdStr = String.valueOf(chatId);
        sendMessage(chatIdStr, formattedText);
    }

    public void sendLowerTempNotification(NotificationDto notificationDto) {
        String message = TEMPERATURE_NOTIFICATION.lowerTempNotification(
            notificationDto.getDeviceToken(),
            notificationDto.getDeviceType(),
            notificationDto.getValue());
        sendMessage(notificationDto.getChatId(), message);
    }

    public void sendHighTempNotification(NotificationDto notificationDto) {
        String message = TEMPERATURE_NOTIFICATION.highTempNotification(
            notificationDto.getDeviceToken(),
            notificationDto.getDeviceType(),
            notificationDto.getValue());
        sendMessage(notificationDto.getChatId(), message);
    }

    public void sendEqualTempNotification(NotificationDto notificationDto) {
        String message = TEMPERATURE_NOTIFICATION.equalTempNotification(
            notificationDto.getDeviceToken(),
            notificationDto.getDeviceType(),
            notificationDto.getValue());
        sendMessage(notificationDto.getChatId(), message);
    }

    private void sendMessage(String chatId, String text) {
        var sendMessage = new SendMessage(chatId, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return null;
    }
}
