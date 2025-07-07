package ru.rainir.task_list_telegram.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rainir.task_list_telegram.Model.Task;

import java.util.List;

@Service
public class TaskApiService {

    @Value("${task.api.url}")
    private String taskApiUrl;

    @Value("${telegramUser.api.url}")
    private String telegramApiUrl;

    private final WebClient webClient;

    public TaskApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Task getTaskById(String taskId) {
        return webClient.get()
                .uri(taskApiUrl + "/" + taskId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Task.class).block();
    }

    public List<Task> getTasksByTelegramId(Long telegramId) {
        Long userId = webClient.get()
                .uri(telegramApiUrl + "/getUserId?telegramId=" + telegramId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Long.class).block();

        return webClient.get()
                .uri(taskApiUrl + "?authorId=" + userId)
                .retrieve()
                .toEntityList(Task.class)
                .map(responseEntities -> {
                    assert responseEntities.getBody() != null;
                    return responseEntities.getBody().stream()
                            .toList();
                }).block();
    }

    public Task createTask(Task task) {
        return webClient.post()
                .uri(taskApiUrl + "/create")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(getMultipartFormTask(task))
                .retrieve()
                .bodyToMono(Task.class)
                .block();
    }

    private MultiValueMap<String, String> getMultipartFormTask(Task task) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("authorId", String.valueOf(task.getAuthorId()));
        multiValueMap.add("title", task.getTitle());
        multiValueMap.add("description", task.getDescription());
        multiValueMap.add("priority", String.valueOf(task.getPriority()));
        multiValueMap.add("completedAt", task.getCompletedAt().toString());

        System.out.println(multiValueMap);
        return multiValueMap;
    }
}
