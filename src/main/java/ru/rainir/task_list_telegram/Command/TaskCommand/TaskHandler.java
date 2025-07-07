package ru.rainir.task_list_telegram.Command.TaskCommand;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.rainir.task_list_telegram.Model.Task;
import ru.rainir.task_list_telegram.Service.TaskApiService;

import java.util.ArrayList;
import java.util.List;

import static ru.rainir.task_list_telegram.Command.InlineKeyboardCreation.*;

@Component
public class TaskHandler {

    public SendMessage taskHandler(Update update, TaskApiService taskApiService) {

        if (update.hasMessage() && update.getMessage().getText().startsWith("/") ) {
            if (update.getMessage().getText().equals("/tasks")) {
                return createTasksTitleListAndInlineKeyBoard(update.getMessage().getChatId(), taskApiService);
            }
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().startsWith("callbackTask_get_")) {
                return getTask(update, taskApiService);
            }
        } else  {
            return new SendMessage(update.getMessage().getChatId().toString(), "Неизвестная команда!");
        }

        return new SendMessage(update.getMessage().getChatId().toString(), "Ошибка запроса!");
    }

    private SendMessage createTasksTitleListAndInlineKeyBoard(Long chatId, TaskApiService taskApiService) {
        String messageText;

        List<Task> tasks = taskApiService.getTasksByTelegramId(chatId);

        if (tasks == null || tasks.isEmpty()) {
            messageText = "Ваш список задач пуст.\n" +
            "Используйте '/task_create' для создания своей первой задачи.";
        } else {
            messageText = "Список ваших задач:\n\n" + createTitleNameMessage(tasks);
        }

        SendMessage message = new SendMessage(chatId.toString(), messageText);

        assert tasks != null;
        message.setReplyMarkup(createTaskListInlineKeyboard(tasks.size()));

        return message;
    }

    private SendMessage getTask(Update update, TaskApiService taskApiService) {
        List<Task> tasks = taskApiService.getTasksByTelegramId(update.getCallbackQuery().getFrom().getId());
        String callback = update.getCallbackQuery().getData();
        return createTaskDetailsMessage(
                        update.getCallbackQuery().getFrom().getId(),
                        tasks.get(Integer.parseInt(callback.split("_")[2]) - 1)
                );
    }

    public SendMessage createTaskDetailsMessage(Long chatId, Task task) {

        String messageText = "Описание таска:\n" + task;

        SendMessage message = new SendMessage(String.valueOf(chatId), messageText);

        message.setReplyMarkup(
                createInlineKeyboardMarkup(
                        createInlineKeyboardRows(
                                createInlineKeyboardRow(
                                        createChangeNameButton(),
                                        createChangeDescriptionButton(),
                                        createUpdateStatusButton()
                                ),
                                createInlineKeyboardRow(
                                        createChangeOtherParametersButton(),
                                        createDeleteTaskButton()
                                ),
                                createInlineKeyboardRow(
                                        backToTaskListButton()
                                )
                        )
                )
        );
        return message;
    }

    private static String createTitleNameMessage(List<Task> tasks) {
        int taskNumber = 1;
        StringBuilder messageText = new StringBuilder();
        for (Task task : tasks) {
            messageText.append(taskNumber).append(". ").append(task.getTitle()).append("\n");
            taskNumber++;
        }
        return messageText.toString();
    }

    private static InlineKeyboardMarkup createTaskListInlineKeyboard(int tasksCount) {
        int taskNumber = 1;
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        List<InlineKeyboardRow> inlineKeyboardRows = new ArrayList<>();
        for (int i = 0; i < tasksCount; i++) {
            buttonList.add(createInlineKeyboardButton(String.valueOf(taskNumber), "callbackTask_get_" + taskNumber));
            taskNumber++;

            if (buttonList.size() % 4 == 0) {
                inlineKeyboardRows.add(createInlineKeyboardRow(buttonList.subList(0, buttonList.size())));
                buttonList = new ArrayList<>();
            }
        }

        if (!buttonList.isEmpty()) {
            inlineKeyboardRows.add(createInlineKeyboardRow(buttonList));
        }

        return new InlineKeyboardMarkup(inlineKeyboardRows);
    }


    private static InlineKeyboardButton createChangeNameButton() {
        return createInlineKeyboardButton("Изменить название",  "callbackTask_change_name");
    }

    private static InlineKeyboardButton createChangeDescriptionButton() {
        return createInlineKeyboardButton("Изменить описание",  "callbackTask_description");
    }

    private static InlineKeyboardButton createUpdateStatusButton() {
        return createInlineKeyboardButton("Обновить статус", "callbackTask_update_status");
    }

    private static InlineKeyboardButton createChangeOtherParametersButton() {
        return createInlineKeyboardButton("Изменить другие параметры таска", "callbackTask_change_other_parameters");
    }

    private static InlineKeyboardButton createDeleteTaskButton() {
        return createInlineKeyboardButton("Удалить задачу", "callbackTask_delete_task");
    }

    private static InlineKeyboardButton backToTaskListButton() {
        return createInlineKeyboardButton("Вернуться к списку задач", "callbackTask_back_to_task_list");
    }
}
