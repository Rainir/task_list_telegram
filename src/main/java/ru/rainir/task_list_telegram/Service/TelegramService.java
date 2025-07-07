package ru.rainir.task_list_telegram.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.rainir.task_list_telegram.Command.GlobalHandler;
import ru.rainir.task_list_telegram.Model.Telegram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramService extends TelegramBotsLongPollingApplication implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private final Telegram telegram;
    private TelegramClient telegramClient;
    private final TaskApiService taskApiService;
    private final TelegramApiService telegramApiService;
    private final GlobalHandler globalHandler;

    private final Map<Long, TaskConversation> conversations = new ConcurrentHashMap<>();

    public TelegramService(Telegram telegram, TaskApiService taskApiService, TelegramApiService telegramApiService, GlobalHandler globalHandler) {
        this.telegram = telegram;
        this.taskApiService = taskApiService;
        this.telegramApiService = telegramApiService;
        this.globalHandler = globalHandler;
        initTelegramClient();
    }

    private void initTelegramClient() {
        telegramClient = new OkHttpTelegramClient(telegram.getBOT_TOKEN());
        log.info("Бот запущен и работает!");
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
        SendMessage message;
        Long chatId = null;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
            System.out.println(update.getCallbackQuery().getFrom().getId() + ": " + update.getCallbackQuery().getData() + " callback");
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            System.out.println(chatId + ": " + update.getMessage().getText());
        }

        message = consumeHandler(update, chatId);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private SendMessage consumeHandler(Update update, Long chatId) {

        TaskConversation conversation = conversations.getOrDefault(chatId, null);

        if (update.hasMessage() && update.getMessage().getText().equals("/task_create") && conversation == null) {
            conversation = new TaskConversation();
            conversations.put(chatId, conversation);
            conversation.startConversation(chatId);
            return new SendMessage(chatId.toString(), "Введите название задачи:");
        } else if (conversation != null && conversation.conversationActive) {
            return conversation.processMessage(update.getMessage().getText(), taskApiService, telegramApiService.getUserIdByTelegramId(chatId));
        }

        if (telegramApiService.checkTelegramUserRegistration(chatId) ||
                (update.hasMessage() && (update.getMessage().getText().equals("/start") || update.getMessage().getText().startsWith("/register"))) ||
                (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("callbackUser_registration"))) {
            return globalHandler.handler(update, telegramApiService, taskApiService);
        } else {
            return new SendMessage(chatId.toString(), "Похоже вы пользуетесь ботом впервые, введите команду  '/start'  чтобы продолжить");
        }
    }
}