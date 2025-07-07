package ru.rainir.task_list_telegram.Command.UserCommand;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rainir.task_list_telegram.Model.TelegramUser;
import ru.rainir.task_list_telegram.Service.TaskApiService;
import ru.rainir.task_list_telegram.Service.TelegramApiService;

import static ru.rainir.task_list_telegram.Command.InlineKeyboardCreation.*;
import static ru.rainir.task_list_telegram.Command.InlineKeyboardCreation.createInlineKeyboardButton;

@Component
public class UserHandler {

    public SendMessage userHandler(Update update, TelegramApiService telegramApiService, TaskApiService taskApiService) {
        if (update.hasCallbackQuery()) {
            return userCallbackHandler(update, telegramApiService);
        } else if (update.hasMessage()) {
            return userCommandHandler(update, telegramApiService, taskApiService);
        }

        return new SendMessage(update.getMessage().getChatId().toString(), "Неизвестное действие!");
    }

    private static SendMessage userCommandHandler(Update update, TelegramApiService telegramApiService,  TaskApiService taskApiService) {

        if (update.getMessage().getText().startsWith("/register")) {
            return userRegistrationOnlyTelegram(update, telegramApiService);
        }

        switch(update.getMessage().getText()) {
            case "/start" -> {
                return registrationStartMassage(update.getMessage().getChatId());
            }

            case "/profile" -> {
                return new SendMessage(
                        update.getMessage().getChatId().toString(),
                        userProfileDetails(update.getMessage().getChatId(), telegramApiService,  taskApiService));
            }
        }
        return new SendMessage(update.getMessage().getChatId().toString(), "Неизвестная команда!");
    }

    private static SendMessage userCallbackHandler(Update update, TelegramApiService telegramApiService) {

       return switch (update.getCallbackQuery().getData()) {
            case "callbackUser_registrationYes" -> userAndIdOnWebRegistrationRequest(update.getCallbackQuery().getFrom().getId());
            case "callbackUser_registrationNo" -> userRegistrationOnlyTelegram(update,  telegramApiService);
            default -> new SendMessage(update.getCallbackQuery().getFrom().getId().toString(), "Что-то пошло не так, попробуйте другую кнопку.");
        };
    }

    public static SendMessage registrationStartMassage(Long telegramId) {
        SendMessage message = new SendMessage(telegramId.toString(),
                """
                        Вы можете получить доступ к своим задачам если ранее регистрировались на сайте.
                        Если аккаунт уже существует, хотите ли вы привязать его к Telegram?
                        """
        );

        message.setReplyMarkup(
                createInlineKeyboardMarkup(
                        createInlineKeyboardRows(
                                createInlineKeyboardRow(
                                        createInlineKeyboardButton("Да, рег. через сайт", "callbackUser_registrationYes"),
                                        createInlineKeyboardButton("Нет, рег. исп тг", "callbackUser_registrationNo"))
                        )));

        return message;
    }

    private static SendMessage userRegistrationOnlyTelegram(Update update, TelegramApiService telegramApiService) {
        TelegramUser telegramUser = new TelegramUser();

        telegramUser.setPassword("randomPass");

        if (update.hasCallbackQuery()) {
            telegramUser.setTelegramId(update.getCallbackQuery().getFrom().getId());
            if (!update.getCallbackQuery().getFrom().getUserName().isEmpty()) {
                telegramUser.setTelegramUsername(update.getCallbackQuery().getFrom().getUserName());
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            telegramUser.setTelegramId(update.getMessage().getChatId());
            if (!update.getMessage().getChat().getUserName().isEmpty()) {
                telegramUser.setTelegramUsername(update.getMessage().getChat().getUserName());
            }
        }

        if (telegramApiService.checkTelegramUserRegistration(telegramUser.getTelegramId())) {
            return new SendMessage(telegramUser.getTelegramId().toString(), "Ошибка! Вы уже зарегистрированы.");
        }

        if (update.hasMessage() && update.getMessage().getText().startsWith("/register")) {

            String[] webUserParams = update.getMessage().getText().split(" ");

                Long userId = Long.parseLong(webUserParams[2]);

                telegramUser.setUserId(userId);
                telegramUser.setUsername(webUserParams[1]);
        } else {
            if (update.hasMessage() && !update.getMessage().getChat().getUserName().isEmpty()) {
                telegramUser.setTelegramUsername(update.getMessage().getChat().getUserName());
                telegramUser.setUsername(update.getMessage().getChat().getUserName());
            }

            if (update.hasCallbackQuery() &&  update.getCallbackQuery().getData().equals("callbackUser_registrationNo")) {
                if (!update.getCallbackQuery().getFrom().getUserName().isEmpty()) {
                    telegramUser.setTelegramUsername(update.getCallbackQuery().getFrom().getUserName());
                    telegramUser.setUsername(update.getCallbackQuery().getFrom().getUserName());
                }
            }
        }

        telegramUser = telegramApiService.telegramUserRegistration(telegramUser);

        assert telegramUser != null;

        return new SendMessage(telegramUser.getTelegramId().toString(), "✅ Вы успешно зарегистрированы! Ваш общий Id: " + telegramUser.getUserId());
    }

    private static SendMessage userAndIdOnWebRegistrationRequest(Long telegramId) {
        return new SendMessage(telegramId.toString(), "Для подключения аккаунта зайдите в личный кабинет на сайте и отправьте в чат сообщение вида\n " +
                "'/register username id', где username - имя вашего профиля, id - ваш id профиля.");
    }

//    private static SendMessage userWebRegistrationRequest() {
//
//    }

    private static String userProfileDetails(Long telegramId, TelegramApiService telegramApiService, TaskApiService taskApiService) {
        TelegramUser telegramUser = telegramApiService.getTelegramUserByTelegramId(telegramId);

        return telegramUser.toString() +
                "Количество ваших задач: " + taskApiService.getTasksByTelegramId(telegramId).size();
    }
}