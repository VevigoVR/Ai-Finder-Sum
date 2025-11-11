package com.example.finder.ai_finder_sum.service;

import com.example.finder.ai_finder_sum.ai.AI;
import com.example.finder.ai_finder_sum.config.AIData;
import com.example.finder.ai_finder_sum.dto.TrainData;
import com.example.finder.ai_finder_sum.train.CreationTrainData;
import com.example.finder.ai_finder_sum.train.TrainProcess;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class MainService {
    private final AIModelManagerService modelManager = new AIModelManagerService();
    private final CreationTrainData creationTrainData = new CreationTrainData();

    public long findSumFromExelFile(MultipartFile file) throws Exception {
        int[] numbers = readNumbersFromExcel(file);
        return findSum(numbers); // 1
        // return findNthSmallestBasic(numbers, n); // 2
        // return findNthSmallest(numbers, n); // 3
    }

    /**
     * Эффективный алгоритм поиска N-ного минимального числа
     * Используем максимальную кучу для хранения N минимальных элементов
     * Сложность: O(m log N), где m - количество чисел, N - параметр
     */
    private long findSum(int[] nums) {
        System.out.println(Arrays.toString(nums));
        if (AIData.getAi().getValidationErrors() == null || AIData.getAi().getValidationErrors().isEmpty()) {
            TrainProcess trainProccess = new TrainProcess();
            AIData.setAi(trainProccess.train());
            // После обучения сохраняем модель
            String savedFilename = modelManager.saveModelWithTimestamp(AIData.getAi());
            System.out.println("Модель сохранена как: " + savedFilename);
        }
        long result = AIData.getAi().predict(nums);
        return result;
    }

    private int[] readNumbersFromExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            int[] numbers = new int[rowCount];
            int validCount = 0;

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    numbers[validCount++] = (int) cell.getNumericCellValue();
                }
            }

            // Возвращаем массив только с валидными числами
            if (validCount < numbers.length) {
                int[] result = new int[validCount];
                System.arraycopy(numbers, 0, result, 0, validCount);
                return result;
            }

            return numbers;
        }
    }

    // Метод для загрузки и тестирования существующей модели
    public void testSavedModel() {
        AI loadedAI = modelManager.loadLatestModel();
        if (loadedAI == null) {
            System.out.println("Не удалось загрузить модель для тестирования");
            return;
        }

        System.out.println("\n=== ТЕСТИРОВАНИЕ ЗАГРУЖЕННОЙ МОДЕЛИ ===");
        System.out.println(loadedAI.getModelInfo());

        // Создаём тестовые данные
        List<TrainData> testData = creationTrainData.create(10, 9, 1, 100);

        System.out.println("\nТестовые предсказания:");
        for (TrainData example : testData) {
            int prediction = loadedAI.predict(example.getNums());
            System.out.printf("Вход: %s → Предсказано: %d, Ожидалось: %d %s%n",
                    Arrays.toString(example.getNums()),
                    prediction, example.getExpectedOutput(),
                    prediction == example.getExpectedOutput() ? "✅" : "❌");
        }
    }
}