package com.example.finder;

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
public class NMinController {

    private final NMinimumService nMinimumService;

    @Operation(summary = "Найти N-ное минимальное число из Excel файла")
    @PostMapping(value = "/n-min", consumes = "multipart/form-data")
    public ResponseEntity<?> findNthMinimum(
            @Parameter(description = "Excel файл с числами") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Порядковый номер минимального числа") @RequestParam("n") int n) {

        try {
            if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Файл должен быть в формате XLSX");
            }
            int result = nMinimumService.findNthMinimum(file, n);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка обработки файла: " + e.getMessage());
        }
    }
}