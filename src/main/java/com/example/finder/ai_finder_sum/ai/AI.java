package com.example.finder.ai_finder_sum.ai;

import com.example.finder.ai_finder_sum.dto.TrainData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
@NoArgsConstructor
public class AI implements Serializable {
    private double[][] weights1;
    private double[][] weights2;
    private double learningRate;
    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    private final int MAX_INPUT = 100;
    private final int MAX_OUTPUT = 1000;

    @Getter
    private List<Double> trainingErrors = new ArrayList<>();
    @Getter
    private List<Double> validationErrors = new ArrayList<>();

    public AI(int inputSize, int hiddenSize, int outputSize, double learningRate) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;

        this.weights1 = new double[inputSize][hiddenSize];
        this.weights2 = new double[hiddenSize][outputSize];
        initializeWeights();
    }

    private void initializeWeights() {
        Random rand = new Random();
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weights1[i][j] = rand.nextDouble() * 0.1 - 0.05; // меньший диапазон
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights2[i][j] = rand.nextDouble() * 0.1 - 0.05;
            }
        }
    }

    // Линейная функция активации на выходе для регрессии
    private double linear(double x) {
        return x;
    }

    // Производная линейной функции
    private double linearDerivative() {
        return 1.0;
    }

    // Сигмоида для скрытого слоя
    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    // Производная сигмоиды
    private double sigmoidDerivative(double x) {
        return x * (1 - x);
    }

    // Прямое распространение
    public double[] forward(double[] input) {
        double[] hidden = new double[hiddenSize];

        // Вычисляем скрытый слой с сигмоидой
        for (int j = 0; j < hiddenSize; j++) {
            for (int i = 0; i < inputSize; i++) {
                hidden[j] += input[i] * weights1[i][j];
            }
            hidden[j] = sigmoid(hidden[j]);
        }

        // Вычисляем выходной слой с ЛИНЕЙНОЙ активацией
        double[] output = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            for (int i = 0; i < hiddenSize; i++) {
                output[j] += hidden[i] * weights2[i][j];
            }
            output[j] = linear(output[j]); // Линейная активация!
        }

        return output;
    }

    // Обучение сети
    public double train(double[] input, double target) {
        // Прямое распространение
        double[] hidden = new double[hiddenSize];
        double[] hiddenOutput = new double[hiddenSize];

        for (int j = 0; j < hiddenSize; j++) {
            for (int i = 0; i < inputSize; i++) {
                hidden[j] += input[i] * weights1[i][j];
            }
            hiddenOutput[j] = sigmoid(hidden[j]);
        }

        double[] output = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            for (int i = 0; i < hiddenSize; i++) {
                output[j] += hiddenOutput[i] * weights2[i][j];
            }
            output[j] = linear(output[j]); // Линейная активация!
        }

        // Вычисляем ошибку (MSE)
        double error = 0.0;
        for (int i = 0; i < outputSize; i++) {
            error += Math.pow(target - output[i], 2);
        }
        error /= outputSize;

        // Обратное распространение ошибки
        double[] outputError = new double[outputSize];
        double[] outputDelta = new double[outputSize];

        for (int i = 0; i < outputSize; i++) {
            outputError[i] = target - output[i];
            outputDelta[i] = outputError[i] * linearDerivative(); // Линейная производная!
        }

        double[] hiddenError = new double[hiddenSize];
        double[] hiddenDelta = new double[hiddenSize];

        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                hiddenError[i] += outputDelta[j] * weights2[i][j];
            }
            hiddenDelta[i] = hiddenError[i] * sigmoidDerivative(hiddenOutput[i]);
        }

        // Обновление весов
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights2[i][j] += hiddenOutput[i] * outputDelta[j] * learningRate;
            }
        }

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weights1[i][j] += input[i] * hiddenDelta[j] * learningRate;
            }
        }

        return error;
    }

    // Основной метод для предсказания
    public int predict(int[] nums) {
        double[] input = prepareInput(nums);
        double[] output = forward(input);

        // Округляем до ближайшего целого
        return denormalize(output[0], 1, MAX_INPUT);
    }

    // Подготовка входных данных - УЛУЧШЕННАЯ нормализация
    public double[] prepareInput(int[] nums) {
        double[] input = new double[inputSize];

        // Нормализуем числа в диапазон [0, 1] на основе ожидаемого диапазона
        int i = 0;
        for (int num : nums) {
            if (i < inputSize) {
                input[i] = normalize(num, 1, MAX_OUTPUT); // предполагаемый диапазон [0, 100]
                i++;
            } else {
                break;
            }
        }
        return input;
    }

    // Улучшенная нормализация в диапазон [0, 1]
    private double normalize(int value, int min, int max) {
        return (double)(value - min) / (max - min);
    }

    // Денормализация
    private int denormalize(double value, int min, int max) {
        return (int) Math.round(value * (max - min) + min);
    }

    // Метод для обучения на нескольких примерах
    public double trainBatch(List<TrainData> examples) {
        // Перемешиваем примеры для лучшего обучения
        Collections.shuffle(examples);

        double totalError = 0.0;
        for (TrainData example : examples) {
            double[] input = prepareInput(example.getNums());
            // Целевое значение нормализуем в тот же диапазон, что и входы
            double target = normalize(example.getExpectedOutput(), 1, MAX_INPUT);
            double error = train(input, target);
            totalError += error;
        }
        double averageError = totalError / examples.size();
        trainingErrors.add(averageError);
        return averageError;
    }

    // Метод для валидации
    public ValidationResult validate(List<TrainData> validationData) {
        double totalError = 0.0;
        int correctPredictions = 0;
        int totalExamples = validationData.size();

        for (TrainData example : validationData) {
            double[] input = prepareInput(example.getNums());
            double[] output = forward(input);
            double target = normalize(example.getExpectedOutput(), 1, MAX_INPUT);

            double error = Math.pow(target - output[0], 2); // MSE для одного выхода
            totalError += error;

            // Проверяем точность предсказания - используем тот же метод, что и в predict()
            int prediction = predict(example.getNums()); // Используем публичный метод!
            if (prediction == example.getExpectedOutput()) {
                correctPredictions++;
            }
        }

        double averageError = totalError / totalExamples;
        double accuracy = (double) correctPredictions / totalExamples;
        validationErrors.add(averageError);

        return new ValidationResult(averageError, accuracy, correctPredictions, totalExamples);
    }

    // Вложенный класс для результатов валидации
    @Getter
    public static class ValidationResult {
        private final double error;
        private final double accuracy;
        private final int correctPredictions;
        private final int totalExamples;

        public ValidationResult(double error, double accuracy, int correctPredictions, int totalExamples) {
            this.error = error;
            this.accuracy = accuracy;
            this.correctPredictions = correctPredictions;
            this.totalExamples = totalExamples;
        }

        @Override
        public String toString() {
            return String.format("ValidationResult{error=%.6f, accuracy=%.2f%%, correct=%d/%d}",
                    error, accuracy * 100, correctPredictions, totalExamples);
        }
    }

    // Класс для хранения статистики обучения
    @Getter
    public static class TrainingStats {
        private final List<Double> trainingErrors;
        private final List<Double> validationErrors;
        private final double minTrainingError;
        private final double maxTrainingError;
        private final double avgTrainingError;
        private final double currentTrainingError;
        private final double currentValidationError;

        public TrainingStats(List<Double> trainingErrors, List<Double> validationErrors) {
            this.trainingErrors = new ArrayList<>(trainingErrors);
            this.validationErrors = new ArrayList<>(validationErrors);

            this.minTrainingError = trainingErrors.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            this.maxTrainingError = trainingErrors.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            this.avgTrainingError = trainingErrors.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            this.currentTrainingError = trainingErrors.isEmpty() ? 0.0 : trainingErrors.get(trainingErrors.size() - 1);
            this.currentValidationError = validationErrors.isEmpty() ? 0.0 : validationErrors.get(validationErrors.size() - 1);
        }
    }

    // Метод для сохранения ИИ в файл
    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("ИИ успешно сохранён в файл: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ошибка при сохранении ИИ: " + e.getMessage());
        }
    }

    // Статический метод для загрузки ИИ из файла
    public static AI loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            AI loadedAI = (AI) ois.readObject();
            System.out.println("ИИ успешно загружен из файла: " + filename);

            // Восстанавливаем transient поля
            loadedAI.trainingErrors = new ArrayList<>();
            loadedAI.validationErrors = new ArrayList<>();

            return loadedAI;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке ИИ: " + e.getMessage());
            return null;
        }
    }

    // Метод для получения информации о модели (полезно при загрузке)
    public String getModelInfo() {
        return String.format(
                "AI Model Info:\n" +
                        "- Архитектура: %d входов -> %d скрытых -> %d выходов\n" +
                        "- Learning rate: %.4f\n" +
                        "- Размеры весов: %dx%d, %dx%d",
                inputSize, hiddenSize, outputSize, learningRate,
                weights1.length, weights1[0].length,
                weights2.length, weights2[0].length
        );
    }
}