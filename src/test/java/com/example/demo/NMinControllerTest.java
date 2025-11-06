package com.example.demo;

import com.example.finder.NMinController;
import com.example.finder.NMinimumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Интеграционные тесты NMinController")
class NMinControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NMinimumService nMinService;

    private NMinController nMinController;

    @BeforeEach
    void setUp() {
        nMinController = new NMinController(nMinService);
        mockMvc = MockMvcBuilders.standaloneSetup(nMinController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
    }

    @Test
    @DisplayName("Успешный поиск N-ного минимального числа")
    void testFindNthMin_Success() throws Exception {
        // Arrange
        MockMultipartFile file = createTestExcelFile();
        when(nMinService.findNthMinimum(any(MockMultipartFile.class), eq(3)))
                .thenReturn(15);

        // Act & Assert
        mockMvc.perform(multipart("/api/n-min")
                        .file(file)
                        .param("n", "3")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    @DisplayName("Ошибка при загрузке файла неправильного формата")
    void testFindNthMin_InvalidFileType() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "fake content".getBytes()
        );

        // Act & Assert - проверяем только статус, без проверки текста
        mockMvc.perform(multipart("/api/n-min")
                        .file(file)
                        .param("n", "3")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ошибка при отсутствии файла в запросе")
    void testFindNthMin_MissingFile() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/api/n-min")
                        .param("n", "3")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ошибка при отсутствии параметра N")
    void testFindNthMin_MissingNParameter() throws Exception {
        // Arrange
        MockMultipartFile file = createTestExcelFile();

        // Act & Assert
        mockMvc.perform(multipart("/api/n-min")
                        .file(file)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обработка исключения сервиса - N больше размера массива")
    void testFindNthMin_ServiceThrowsException() throws Exception {
        // Arrange
        MockMultipartFile file = createTestExcelFile();
        when(nMinService.findNthMinimum(any(MockMultipartFile.class), eq(10)))
                .thenThrow(new IllegalArgumentException("N too large"));

        // Act & Assert - проверяем только статус
        mockMvc.perform(multipart("/api/n-min")
                        .file(file)
                        .param("n", "10")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ошибка при невалидном значении N (ноль или отрицательное)")
    void testFindNthMin_InvalidNValue() throws Exception {
        // Arrange
        MockMultipartFile file = createTestExcelFile();
        when(nMinService.findNthMinimum(any(MockMultipartFile.class), eq(0)))
                .thenThrow(new IllegalArgumentException("N must be positive"));

        // Act & Assert - проверяем только статус
        mockMvc.perform(multipart("/api/n-min")
                        .file(file)
                        .param("n", "0")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешная обработка файла с одним числом")
    void testFindNthMin_SingleNumberFile() throws Exception {
        // Arrange
        MockMultipartFile file = createTestExcelFile();
        when(nMinService.findNthMinimum(any(MockMultipartFile.class), eq(1)))
                .thenReturn(42);

        // Act & Assert
        mockMvc.perform(multipart("/api/n-min")
                        .file(file)
                        .param("n", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    @DisplayName("Обработка внутренней ошибки сервера")
    void testFindNthMin_InternalServerError() throws Exception {
        // Arrange
        MockMultipartFile file = createTestExcelFile();
        when(nMinService.findNthMinimum(any(MockMultipartFile.class), eq(2)))
                .thenThrow(new RuntimeException("Internal error"));

        // Act & Assert - проверяем только статус
        mockMvc.perform(multipart("/api/n-min")
                        .file(file)
                        .param("n", "2")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Создает тестовый Excel файл для использования в нескольких тестах
     */
    private MockMultipartFile createTestExcelFile() {
        return new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "fake excel content".getBytes(StandardCharsets.UTF_8)
        );
    }
}