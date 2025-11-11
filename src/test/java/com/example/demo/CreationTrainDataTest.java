package com.example.demo;

import com.example.finder.ai_finder_sum.dto.TrainData;
import com.example.finder.ai_finder_sum.train.CreationTrainData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreationTrainDataTest {

    private CreationTrainData creationTrainData;

    @BeforeEach
    void setUp() {
        creationTrainData = new CreationTrainData();
    }

    @Test
    @DisplayName("Создание указанного количества TrainData")
    void create_ShouldReturnCorrectQuantity() {
        // Given
        int quantity = 5;
        int size = 10;
        int min = 1;
        int max = 100;

        // When
        List<TrainData> result = creationTrainData.create(quantity, size, min, max);

        // Then
        assertNotNull(result);
        assertEquals(quantity, result.size());
    }

    @Test
    @DisplayName("Создание TrainData с правильной структурой")
    void createOne_ShouldReturnValidTrainData() {
        // Given
        int size = 5;
        int min = 1;
        int max = 50;

        // When
        TrainData trainData = creationTrainData.createOne(size, min, max);

        // Then
        assertNotNull(trainData);
        assertNotNull(trainData.getNums());
        assertEquals(size, trainData.getNums().length);
        assertTrue(trainData.getExpectedOutput() >= min && trainData.getExpectedOutput() <= max);
    }






    @Test
    @DisplayName("Создание списка чисел правильного размера")
    void createNums_ShouldReturnListWithCorrectSize() {
        // Given
        int size = 7;
        int min = 10;
        int max = 20;

        // When
        List<Integer> result = creationTrainData.createNums(size, min, max);

        // Then
        assertNotNull(result);
        assertEquals(size, result.size());
    }

    @Test
    @DisplayName("Создание списка чисел в заданном диапазоне")
    void createNums_ShouldReturnNumbersInRange() {
        // Given
        int size = 100; // Большой размер для статистической проверки
        int min = 5;
        int max = 15;

        // When
        List<Integer> result = creationTrainData.createNums(size, min, max);

        // Then
        assertNotNull(result);

        // Проверяем что все числа в диапазоне
        for (int num : result) {
            assertTrue(num >= min && num <= max,
                    "Число " + num + " должно быть в диапазоне [" + min + ", " + max + "]");
        }

        // Проверяем что есть числа близкие к границам (статистически вероятно)
        boolean hasMin = result.stream().anyMatch(n -> n == min);
        boolean hasMax = result.stream().anyMatch(n -> n == max);

        // В большом списке статистически вероятно иметь оба граничных значения
        assertTrue(hasMin || hasMax, "Должны присутствовать числа близкие к границам диапазона");
    }

    @Test
    @DisplayName("Генерация чисел в заданном диапазоне")
    void generate_ShouldReturnNumberInRange() {
        // Given
        int min = 25;
        int max = 35;

        // When & Then - многократная проверка
        for (int i = 0; i < 1000; i++) {
            int result = creationTrainData.generate(min, max);
            assertTrue(result >= min && result <= max,
                    "Сгенерированное число " + result + " должно быть в диапазоне [" + min + ", " + max + "]");
        }
    }

    @Test
    @DisplayName("Генерация при min = max")
    void generate_WhenMinEqualsMax_ShouldReturnThatValue() {
        // Given
        int min = 42;
        int max = 42;

        // When
        int result = creationTrainData.generate(min, max);

        // Then
        assertEquals(42, result);
    }

    @Test
    @DisplayName("Создание TrainData с различными размерами")
    void create_WithDifferentSizes_ShouldWorkCorrectly() {
        // Test with small size
        List<TrainData> small = creationTrainData.create(3, 1, 1, 10);
        assertEquals(3, small.size());
        assertEquals(1, small.get(0).getNums().length);

        // Test with larger size
        List<TrainData> large = creationTrainData.create(2, 100, 1, 1000);
        assertEquals(2, large.size());
        assertEquals(100, large.get(0).getNums().length);
    }
}