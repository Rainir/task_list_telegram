package ru.rainir.task_list_telegram.Command.TaskCommand;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.rainir.task_list_telegram.Model.Task;

public class InlineKeyboardHelper {

    private static final Task task = new Task();

    public static SendMessage createTaskDetailsMessage(long chatId) {


        String messageText = "Описание таска:\n" + task;

        SendMessage message = new SendMessage(String.valueOf(chatId), messageText);

        InlineKeyboardRow firstRow = new InlineKeyboardRow();
        InlineKeyboardRow secondRow = new InlineKeyboardRow();
        InlineKeyboardRow thirdRow = new InlineKeyboardRow();

        List<InlineKeyboardRow> rowsInline = new ArrayList<>();

        firstRow.add(createChangeNameButton());
        firstRow.add(createChangeDescriptionButton());
        firstRow.add(createUpdateStatusButton());

        secondRow.add(createChangeOtherParametersButton());
        secondRow.add(createDeleteTaskButton());

        thirdRow.add(backToTaskListButton());

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(rowsInline);

        keyboardMarkup.getKeyboard().add(firstRow);
        keyboardMarkup.getKeyboard().add(secondRow);
        keyboardMarkup.getKeyboard().add(thirdRow);

        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private static InlineKeyboardButton createChangeNameButton() {
        return InlineKeyboardButton.builder()
                .text("Изменить название")
                .callbackData("change_name") // Используйте уникальные callback data
                .build();
    }

    private static InlineKeyboardButton createChangeDescriptionButton() {
        return InlineKeyboardButton.builder()
                .text("Изменить описание")
                .callbackData("change_description")
                .build();
    }

    private static InlineKeyboardButton createUpdateStatusButton() {
        return InlineKeyboardButton.builder()
                .text("Обновить статус")
                .callbackData("update_status")
                .build();
    }

    private static InlineKeyboardButton createChangeOtherParametersButton() {
        return InlineKeyboardButton.builder()
                .text("Изменить другие параметры таска")
                .callbackData("change_other_parameters")
                .build();
    }

    private static InlineKeyboardButton createDeleteTaskButton() {
        return InlineKeyboardButton.builder()
                .text("Удалить таск")
                .callbackData("delete_task")
                .build();
    }

    private static InlineKeyboardButton backToTaskListButton() {
        return InlineKeyboardButton.builder()
                .text("Вернуться к списку задач")
                .callbackData("back_to_task_list")
                .build();
    }
}
