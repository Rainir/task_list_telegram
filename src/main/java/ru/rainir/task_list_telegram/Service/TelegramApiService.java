package ru.rainir.task_list_telegram.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import ru.rainir.task_list_telegram.Model.TelegramUser;

@Service
public class TelegramApiService {

    @Value("${telegramUser.api.url}")
    private String telegramUserApiUrl;

    private final WebClient webClient;

    public TelegramApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Boolean checkTelegramUserRegistration(Long telegramUserId) {
        return webClient.get()
                .uri(telegramUserApiUrl + "/checkTelegramUser?telegramId=" + telegramUserId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public TelegramUser telegramUserRegistration(TelegramUser telegramUser) {

        return webClient.post()
                .uri(telegramUserApiUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(getMultipartFormTelegramUser(telegramUser))
                .retrieve()
                .bodyToMono(TelegramUser.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Ошибка регистрации!"));
    }

    public TelegramUser getTelegramUserByTelegramId(Long telegramUserId) {
        return webClient.get()
                .uri(telegramUserApiUrl + "/getTelegramUser?telegramId=" + telegramUserId)
                .exchangeToMono(response -> response.toEntity(TelegramUser.class))
                .mapNotNull(HttpEntity::getBody)
                .block();
    }

    public Long getUserIdByTelegramId(Long telegramUserId) {
        return webClient.get()
                .uri(telegramUserApiUrl + "/getUserId?telegramId=" + telegramUserId)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    private MultiValueMap<String, String> getMultipartFormTelegramUser(TelegramUser telegramUser) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("telegramId", String.valueOf(telegramUser.getTelegramId()));
        multiValueMap.add("telegramUsername", telegramUser.getTelegramUsername());

        if (telegramUser.getUserId() != null) {
            multiValueMap.add("userId", String.valueOf(telegramUser.getUserId()));
        }
        if (telegramUser.getUsername() != null) {
            multiValueMap.add("username", telegramUser.getUsername());
        }
        if (telegramUser.getPassword() != null) {
            multiValueMap.add("password", telegramUser.getPassword());
        }
        System.out.println(multiValueMap);
        return multiValueMap;
    }
}