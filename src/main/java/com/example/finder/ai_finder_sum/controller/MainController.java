package com.example.finder.ai_finder_sum.controller;

import com.example.finder.ai_finder_sum.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {

    private final MainService nMinimumService;

    @Operation(summary = "Найти сумму чисел из одного столбца размеров 10 ячеек и в диапазоне от 0 до 100 из Excel файла")
    @PostMapping(value = "/sum", consumes = "multipart/form-data")
    public ResponseEntity<?> findSum(
            @Parameter(description = "Excel файл с числами") @RequestParam("file") MultipartFile file) {

        try {
            if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Файл должен быть в формате XLSX");
            }
            long result = nMinimumService.findSumFromExelFile(file);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Ошибка обработки файла: " + e.getMessage());
        }
    }
}