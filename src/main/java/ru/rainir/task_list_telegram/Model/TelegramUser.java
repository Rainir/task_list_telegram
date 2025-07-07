package ru.rainir.task_list_telegram.Model;

import lombok.*;

@Data
public class TelegramUser {

    private Long userId;
    private String username;
    private String password;

    private Long telegramId;

    private String telegramUsername;

    @Override
    public String toString() {
        return "Ваш Telegram Id: " + telegramId + "\n" +
                "Ваш общий Id: " + userId + "\n" +
                "Общий username: " + username + "\n";
    }
}