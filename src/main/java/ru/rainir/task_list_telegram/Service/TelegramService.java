package ru.rainir.task_list_telegram.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.rainir.task_list_telegram.Command.TaskCommand.InlineKeyboardHelper;
import ru.rainir.task_list_telegram.Command.UserCommand.StartCommand;
import ru.rainir.task_list_telegram.Model.Telegram;

@Service
public class TelegramService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final Telegram telegram;
    private final TelegramClient telegramClient;

    public TelegramService(Telegram telegram, OkHttpTelegramClient getTelegramHttpClient) {
        this.telegram = telegram;
        this.telegramClient = getTelegramHttpClient;
    }

    @PostConstruct
    public void botStarted() {
        try (TelegramBotsLongPollingApplication botApplication = new TelegramBotsLongPollingApplication()) {
            botApplication.registerBot(telegram.getBOT_TOKEN(), this);
            System.out.println("Bot started");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotToken() {
        return telegram.getBOT_TOKEN();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {


        System.out.println(update.getMessage().getChatId() + ": " + update.getMessage().getText());

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            SendMessage message = switch (text) {
                case "/start" -> StartCommand.registrationStartMassage(chat_id);
                case "/task" -> InlineKeyboardHelper.createTaskDetailsMessage(chat_id);
                default -> null;
            };

            try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
}