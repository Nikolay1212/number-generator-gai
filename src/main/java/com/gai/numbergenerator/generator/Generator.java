package com.gai.numbergenerator.generator;

import com.gai.numbergenerator.models.Letters;
import com.gai.numbergenerator.models.Number;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class Generator {

    private String[] letters = {"А", "Е", "Т", "О", "Р", "Н", "У", "К", "Х", "С", "В", "М"};

    private final Random random;

    public Generator() {
        this.random = new Random();
    }

    public Letters generateLetters() {
        String letter = letters[random.nextInt(letters.length)] + "" + letters[random.nextInt(letters.length)] + "" + letters[random.nextInt(letters.length)];
        return Letters.builder()
                .letters(letter)
                .build();
    }

    public String generateNextLetters(String lastLetters) {
        char[] lastLettersArray = lastLetters.toCharArray();
        return lettersCounter(lastLettersArray);
    }

    private String lettersCounter(char[] lastLettersArray) {

        int firstDigitPlace = 0;
        int secondDigitPlace = 0;
        int thirdDigitPlace = 0;
        for (int i = 0; i < letters.length; i++) {
            if (Integer.valueOf(lastLettersArray[2]) < Integer.valueOf(letters[i].charAt(0))) {
                lastLettersArray[2] = letters[i].charAt(0);
                firstDigitPlace ++;
                break;
            }
        }
        if (firstDigitPlace > 0) {
            return String.valueOf(lastLettersArray);
        } else {
            for (int i = 0; i < letters.length; i++) {
                if (Integer.valueOf(lastLettersArray[1]) < Integer.valueOf(letters[i].charAt(0))) {
                    lastLettersArray[1] = letters[i].charAt(0);
                    lastLettersArray[2] = 'А';
                    secondDigitPlace ++;
                    break;
                }
            }
        }
        if (secondDigitPlace > 0) {
            return String.valueOf(lastLettersArray);
        } else {
            for (int i = 0; i < letters.length; i++) {
                if (Integer.valueOf(lastLettersArray[0]) < Integer.valueOf(letters[i].charAt(0))) {
                    lastLettersArray[0] = letters[i].charAt(0);
                    lastLettersArray[1] = 'А';
                    lastLettersArray[2] = 'А';
                    thirdDigitPlace ++;
                    break;
                }
            }
        }
        if (thirdDigitPlace > 0) {
            return String.valueOf(lastLettersArray);
        } else {
            return null;
        }
    }
}
