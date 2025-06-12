package ru.rainir.task_list_telegram.Model;

import lombok.*;

@Data
public class TelegramUser {

    private Long id;
    private String username;

    private Long telegramId;

    private String telegramUsername;
}
