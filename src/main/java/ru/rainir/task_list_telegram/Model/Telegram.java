package ru.rainir.task_list_telegram.Model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Telegram {

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;

    @Value("${telegram.bot.name}")
    private String BOT_NAME;

    public Telegram() {}
}