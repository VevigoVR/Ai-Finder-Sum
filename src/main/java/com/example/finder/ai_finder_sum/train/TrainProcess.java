package com.example.finder.ai_finder_sum.train;

import com.example.finder.ai_finder_sum.ai.AI;
import com.example.finder.ai_finder_sum.dto.TrainData;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@NoArgsConstructor
public class TrainProcess {
    private final CreationTrainData creationTrainData = new CreationTrainData();

    public AI train() {
        // Начнем с более консервативных параметров
        AI ai = new AI(10, 512, 1, 0.01); // 50 нейронов, learning rate 0.05

        List<TrainData> allData = creationTrainData.create(55000, 10, 1, 100);
        List<TrainData> trainingData = allData.subList(0, 50000);
        List<TrainData> validationData = allData.subList(50000, 55000);

        System.out.println("Начало обучения...");
        System.out.println("Архитектура: 10 входов -> 50 скрытых -> 1 выход (линейный)");
        System.out.println("Learning rate: 0.001");
        System.out.println();

        // Покажем несколько примеров ДО обучения
        System.out.println("Примеры ДО обучения:");
        for (int i = 0; i < 3; i++) {
            TrainData example = validationData.get(i);
            int prediction = ai.predict(example.getNums());
            double[] input = ai.prepareInput(example.getNums());
            double[] output = ai.forward(input);
            System.out.printf("Вход: %s → Raw: %.3f → Предсказано: %d, Ожидалось: %d %s%n",
                    Arrays.toString(example.getNums()),
                    output[0], prediction, example.getExpectedOutput(),
                    prediction == example.getExpectedOutput() ? "✅" : "❌");
        }
        System.out.println();

        printStatsHeader();

        double bestValidationError = Double.MAX_VALUE;
        int noImprovementCount = 0;

        for (int epoch = 0; epoch <= 2500; epoch++) {
            double trainingError = ai.trainBatch(trainingData);
            AI.ValidationResult validationResult = ai.validate(validationData);

            if (validationResult.getError() < bestValidationError) {
                bestValidationError = validationResult.getError();
                noImprovementCount = 0;
            } else {
                noImprovementCount++;
            }

            if (epoch % 1 == 0) {
                printEpochStats(epoch, trainingError, validationResult);
            }

            // Ранняя остановка если нет улучшений 100 эпох
            if (noImprovementCount > 100000) {
                System.out.println("Ранняя остановка на эпохе " + epoch + " - нет улучшений 100 эпох");
                break;
            }

            if (validationResult.getAccuracy() == 1) {
                System.out.println("Обучение завершено на эпохе " + epoch + " - достигнута высокая точность!");
                break;
            }
        }

        printFinalStats(ai, validationData);
        return ai;
    }

    private void printStatsHeader() {
        System.out.println("Эпоха\tОшибка обуч.\tОшибка вал.\tТочность\tПравильно/Всего");
        System.out.println("------\t------------\t-----------\t--------\t----------------");
    }

    private void printEpochStats(int epoch, double trainingError, AI.ValidationResult validationResult) {
        System.out.printf("%d\t%.6f\t%.6f\t%.2f%%\t\t%d/%d%n",
                epoch, trainingError, validationResult.getError(),
                validationResult.getAccuracy() * 100,
                validationResult.getCorrectPredictions(),
                validationResult.getTotalExamples());
    }

    private void printFinalStats(AI ai, List<TrainData> validationData) {
        AI.ValidationResult finalResult = ai.validate(validationData);

        System.out.println("\n=== ФИНАЛЬНАЯ СТАТИСТИКА ===");
        System.out.printf("Точность на валидации: %.2f%%%n", finalResult.getAccuracy() * 100);
        System.out.printf("Ошибка на валидации: %.6f%n", finalResult.getError());
        System.out.printf("Правильных предсказаний: %d/%d%n",
                finalResult.getCorrectPredictions(), finalResult.getTotalExamples());

        // Показываем несколько примеров предсказаний ПОСЛЕ обучения
        System.out.println("\nПримеры предсказаний ПОСЛЕ обучения:");
        for (int i = 0; i < Math.min(10, validationData.size()); i++) {
            TrainData example = validationData.get(i);
            int prediction = ai.predict(example.getNums());
            double[] input = ai.prepareInput(example.getNums());
            double[] output = ai.forward(input);
            System.out.printf("Вход: %s → Raw: %.3f → Предсказано: %d, Ожидалось: %d %s%n",
                    Arrays.toString(example.getNums()),
                    output[0], prediction, example.getExpectedOutput(),
                    prediction == example.getExpectedOutput() ? "✅" : "❌");
        }
    }
}