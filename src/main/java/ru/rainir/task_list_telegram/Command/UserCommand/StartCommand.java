package ru.rainir.task_list_telegram.Command.UserCommand;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static ru.rainir.task_list_telegram.Command.InlineKeyboardCreation.*;

public class StartCommand {

    public static SendMessage registrationStartMassage(Long chat_id) {

        SendMessage message = new SendMessage(String.valueOf(chat_id),
                "Регистрировались ли вы раньше на сайте?\n"+
                "Вы можете получить доступ к своим задачам\n" +
                "Для этого зайдите в личный кабинет на сайте и отправьте в чат сообщение вида 'Username id'");

        message.setReplyMarkup(
                createInlineKeyboardMarkup(
                        createInlineKeyboardRows(
                                createInlineKeyboardRow(
                                        createInlineKeyboardButton("Да", "yes"),
                                        createInlineKeyboardButton("нет", "no"))
                )));

        return message;
    }
}
