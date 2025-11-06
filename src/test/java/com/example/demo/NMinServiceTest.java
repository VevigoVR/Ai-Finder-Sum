package com.example.demo;

import com.example.finder.NMinimumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

class NMinServiceTest {

    private NMinimumService nthMinService;

    @BeforeEach
    void setUp() {
        nthMinService = new NMinimumService();
    }

    @Test
    void testFindNthMinimum_WithValidData() throws Exception {
        // Arrange
        MultipartFile file = createTestExcelFile(new int[]{15, 5, 25, 10, 20});
        int n = 3;

        // Act
        int result = nthMinService.findNthMinimum(file, n);

        // Assert
        assertEquals(15, result); // В отсортированном виде: 5,10,15,20,25 → 3-е минимальное = 15
    }

    @Test
    void testFindNthMinimum_FirstMinimum() throws Exception {
        // Arrange
        MultipartFile file = createTestExcelFile(new int[]{100, 50, 75, 25});
        int n = 1;

        // Act
        int result = nthMinService.findNthMinimum(file, n);

        // Assert
        assertEquals(25, result); // 1-е минимальное = 25
    }

    @Test
    void testFindNthMinimum_LastMinimum() throws Exception {
        // Arrange
        MultipartFile file = createTestExcelFile(new int[]{10, 20, 30, 40});
        int n = 4;

        // Act
        int result = nthMinService.findNthMinimum(file, n);

        // Assert
        assertEquals(40, result); // 4-е минимальное = 40
    }

    @Test
    void testFindNthMinimum_DuplicateNumbers() throws Exception {
        // Arrange
        MultipartFile file = createTestExcelFile(new int[]{5, 5, 3, 3, 7});
        int n = 3;

        // Act
        int result = nthMinService.findNthMinimum(file, n);

        // Assert
        assertEquals(5, result); // Отсортировано: 3,3,5,5,7 → 3-е минимальное = 5
    }

    @Test
    void testFindNthMinimum_NGreaterThanArraySize() {
        // Arrange
        MultipartFile file = createTestExcelFile(new int[]{1, 2, 3});
        int n = 5;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            nthMinService.findNthMinimum(file, n);
        });

        assertTrue(exception.getMessage().contains("N не может быть больше количества чисел"));
    }

    @Test
    void testFindNthMinimum_InvalidN() {
        // Arrange
        MultipartFile file = createTestExcelFile(new int[]{1, 2, 3});
        int n = 0;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            nthMinService.findNthMinimum(file, n);
        });

        assertTrue(exception.getMessage().contains("N должно быть положительным"));
    }

    @Test
    void testFindNthMinimum_EmptyFile() {
        // Arrange
        MultipartFile file = createTestExcelFile(new int[]{});
        int n = 1;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            nthMinService.findNthMinimum(file, n);
        });

        assertTrue(exception.getMessage().contains("N не может быть больше количества чисел"));
    }

    @Test
    void testFindNthMinimum_LargeDataset() throws Exception {
        // Arrange - создаем большой набор данных
        int[] largeArray = new int[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = 1000 - i; // числа от 1000 до 1
        }
        MultipartFile file = createTestExcelFile(largeArray);
        int n = 500;

        // Act
        int result = nthMinService.findNthMinimum(file, n);

        // Assert - 500-е минимальное число должно быть 500
        assertEquals(500, result);
    }

    private MultipartFile createTestExcelFile(int[] numbers) {
        try {
            return ExcelTestUtils.createMockExcelFile("test.xlsx", numbers);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test Excel file", e);
        }
    }
}
