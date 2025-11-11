package com.example.finder.ai_finder_sum.service;

import com.example.finder.ai_finder_sum.ai.AI;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AIModelManagerService {

    // Сохранить модель с автоматическим именем
    public String saveModelWithTimestamp(AI ai) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "src/main/resources/ai_sum/ai_model_" + timestamp + ".dat";
        ai.saveToFile(filename);
        return filename;
    }

    // Загрузить последнюю модель
    public AI loadLatestModel() {
        File modelsDir = new File("src/main/resources/ai_sum/");
        File[] modelFiles = modelsDir.listFiles((dir, name) -> name.startsWith("ai_model_") && name.endsWith(".dat"));

        if (modelFiles == null || modelFiles.length == 0) {
            System.out.println("Нет сохранённых моделей");
            return null;
        }

        // Находим самую новую модель
        File latestModel = modelFiles[0];
        for (File model : modelFiles) {
            if (model.lastModified() > latestModel.lastModified()) {
                latestModel = model;
            }
        }

        return AI.loadFromFile(latestModel.getName());
    }

    // Получить список всех сохранённых моделей
    public List<String> listSavedModels() {
        List<String> models = new ArrayList<>();
        File modelsDir = new File(".");
        File[] modelFiles = modelsDir.listFiles((dir, name) -> name.startsWith("ai_model_") && name.endsWith(".dat"));

        if (modelFiles != null) {
            for (File modelFile : modelFiles) {
                models.add(modelFile.getName() + " (изменён: " + new Date(modelFile.lastModified()) + ")");
            }
        }

        return models;
    }

    // Удалить модель
    public boolean deleteModel(String filename) {
        File modelFile = new File(filename);
        if (modelFile.exists()) {
            return modelFile.delete();
        }
        return false;
    }
}
