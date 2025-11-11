package com.example.finder.ai_finder_sum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TrainData {
    private int[] nums;
    private int expectedOutput;
}
