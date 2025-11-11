package com.example.finder.ai_finder_sum.train;

import com.example.finder.ai_finder_sum.dto.TrainData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreationTrainData {

    public List<TrainData> create(int quantity, int size, int min, int max) {
        List<TrainData> result = new ArrayList<>();
        for (int i = 0 ; i < quantity; i++) {
            result.add(createOne(size, min, max));
        }
        return result;
    }

    public TrainData createOne(int size, int min, int max) {
        List<Integer> nums = createNums(size, min, max);
        int answer = sum(nums);
        int[] numArray = nums.stream().mapToInt(i -> i).toArray();
        return new TrainData(numArray, answer);
    }

    public int sum(List<Integer> nums) {
        int result = 0;
        for (int num : nums) {
            result += num;
        }
        return result;
    }

    public List<Integer> createNums(int size, int min, int max) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int randomInRange = generate(min, max);
            result.add(randomInRange);
        }
        return result;
    }

    public int generate(int min, int max) {
        // Случайное double от 0.0 до 1.0
        // Целое число в диапазоне [min, max]
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }
}