package application.dto;

import lombok.Data;

@Data
public class NotificationDto {
  String chatId;
  String deviceToken;
  String deviceType;
  String value;
}
