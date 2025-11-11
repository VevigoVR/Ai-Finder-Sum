package com.example.finder.ai_finder_sum.config;

import com.example.finder.ai_finder_sum.ai.AI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AIData {
    @Getter
    @Setter
    private static AI ai;

    public AIData (AI ai) {
        // Проверка на null для каждого сервиса
        if (ai == null) {
            throw new IllegalStateException("Критическая ошибка: сервисы не инициализированы!");
        }
        AIData.ai = ai;
        System.out.println("✅ DataSet успешно инициализирован!");
    }
}
