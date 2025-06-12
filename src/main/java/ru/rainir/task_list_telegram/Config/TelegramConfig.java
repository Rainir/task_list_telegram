package ru.rainir.task_list_telegram.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Configuration
public class TelegramConfig {
    @Value("${telegram.bot.token}")
    String BOT_TOKEN;

    @Bean
    public OkHttpTelegramClient getTelegramHttpClient() {
        return new OkHttpTelegramClient(BOT_TOKEN);
    }
}