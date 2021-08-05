package com.gai.numbergenerator.services;

import com.gai.numbergenerator.generator.Generator;
import com.gai.numbergenerator.models.LastNumber;
import com.gai.numbergenerator.models.Letters;
import com.gai.numbergenerator.models.Number;
import com.gai.numbergenerator.repositories.LastNumberRepository;
import com.gai.numbergenerator.repositories.LettersRepository;
import com.gai.numbergenerator.repositories.NumbersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class NumberServiceImpl implements NumberService {

    @Autowired
    private NumbersRepository numbersRepository;

    @Autowired
    private LettersRepository lettersRepository;

    @Autowired
    private LastNumberRepository lastNumberRepository;

    private static final Integer MAX_SYMBOL_CAPACITY = 1000;
    private static final String LETTERS_BOUND = "ХХХ";
    private static final Integer MAX_NUMBER_CAPACITY = 999;

    private final Random random;

    public NumberServiceImpl() {
        this.random = new Random();
    }

    Generator generator = new Generator();


    private List<Letters> getAllLettersByNumber(Number number) {
        return lettersRepository.findLettersByNumberId(number);
    }

    //generates new Number or return null (if the table is complete)
    private Number generateNewRandomNumber() {

        List<Integer> listForGenerate = new ArrayList<>();
        for (int i = 0; i <= MAX_NUMBER_CAPACITY; i++) {
            listForGenerate.add(i);
        }
        List<Number> numberList = (List<Number>) numbersRepository.findAll();
        List<Integer> numberValuesList = numberList.stream().map(Number::getNums).collect(Collectors.toList());

        listForGenerate.removeIf(numberValuesList::contains);

        if (listForGenerate.size() > 0) {
            Number number = Number.builder()
                    .nums(listForGenerate.get(random.nextInt(listForGenerate.size())))
                    .build();
            numbersRepository.save(number);
            return number;
        } else {
            return null;
        }
    }

    private Number getRandomNumberFromNumbersWithFreeLettersList() {
        List<Number> numberList = (List<Number>) numbersRepository.findAll();
        List<Number> numberListForGenerate = new ArrayList<>();
        for (Number number :
                numberList) {
            if (number.getLettersList().size() < MAX_SYMBOL_CAPACITY) {
                numberListForGenerate.add(number);
            }
        }
        if (numberListForGenerate.size() > 0) {
            int randomIndex = random.nextInt(numberListForGenerate.size());
            return numberListForGenerate.get(randomIndex);
        } else {
            return null;
        }
    }

    //generates new Letters for the Number or return null if there is no space in the number list
    private Letters generateNewLetter(Number number) {

        List<Letters> lettersList = getAllLettersByNumber(number);
        List<String> lettersAsStringList = lettersList.stream().map(Letters::getLetters).collect(Collectors.toList());

        while (lettersList.size() < MAX_SYMBOL_CAPACITY) {
            Letters letters = generator.generateLetters();
            if (!lettersAsStringList.contains(letters.getLetters())) {
                letters.setNumberId(number);
                lettersList.add(letters);
                lettersRepository.save(letters);
                return letters;
            }
        }
        return null;
    }


    private String lettersToNumberGenerate(Number number) {
        Letters letters = generateNewLetter(number);
        if (letters != null) {
            LastNumber lastNumber = LastNumber.builder()
                    .id(1L)
                    .number(number.getNums())
                    .letters(letters.getLetters()).build();
            lastNumberRepository.save(lastNumber);
            return generateResultNumber(number, letters);
        }
        return null;
    }

    @Override
    public String generateRandomNumber() {

        Number number = generateNewRandomNumber();
        if (number != null) {
            return lettersToNumberGenerate(number);
        } else {
            //get Number List where letter list size < 1000
            number = getRandomNumberFromNumbersWithFreeLettersList();
            if (number != null) {
                return lettersToNumberGenerate(number);
            } else {
                return "The numbers ran out";
            }
        }
    }

    private String increaseNums(Integer lastNums, String lastLetters, LastNumber lastNumber) {

        Number number;
        Letters letters;
        Integer newNums = lastNums + 1;
        while (newNums <= MAX_NUMBER_CAPACITY) {
            //check that number is not exist in DB
            if (numbersRepository.findByNums(newNums).isPresent()) {
                number = numbersRepository.findByNums(newNums).get();
                List<Letters> lettersList = number.getLettersList();
                List<String> lettersListAsString = lettersList.stream().map(Letters::getLetters).collect(Collectors.toList());
                boolean flag = true;
                for (String str :
                        lettersListAsString) {
                    if (str.equals(lastLetters)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    letters = Letters.builder()
                            .numberId(number)
                            .letters(lastLetters)
                            .build();
                    lettersRepository.save(letters);
                    lastNumber.setNumber(newNums);
                    lastNumber.setLetters(lastLetters);
                    lastNumberRepository.save(lastNumber);
                    return generateResultNumber(number, letters);
                } else {
                    if (newNums < MAX_NUMBER_CAPACITY) {
                        newNums += 1;
                    } else {
                        newNums = 0;
                        return increaseLetters(newNums, lastLetters, lastNumber);
                    }
                }
            } else {
                number = Number.builder()
                        .nums(newNums)
                        .build();
                numbersRepository.save(number);
                letters = Letters.builder()
                        .numberId(number)
                        .letters(lastLetters)
                        .build();
                lettersRepository.save(letters);
                lastNumber.setNumber(newNums);
                lastNumber.setLetters(lastLetters);
                lastNumberRepository.save(lastNumber);
                return generateResultNumber(number, letters);
            }
        }
        return null;
    }

    private String increaseLetters(Integer lastNums, String lastLetters, LastNumber lastNumber) {
        String newLetters = generator.generateNextLetters(lastLetters);
        lastNums = 0;
        if (newLetters != null && newLetters.compareTo(LETTERS_BOUND) <= 0) {
            Number number;
            Letters letters;
            while (newLetters.compareTo(LETTERS_BOUND) <= 0) {
                Optional<Number> numberOptional = numbersRepository.findByNums(lastNums);
                if (numberOptional.isPresent()) {
                    number = numberOptional.get();
                    List<Letters> lettersList = number.getLettersList();
                    List<String> letterListAsString = lettersList.stream().map(Letters::getLetters).collect(Collectors.toList());
                    for (String str :
                            letterListAsString) {
                        if (str.equals(newLetters)) {
                            return increaseNums(0, newLetters, lastNumber);
                        }
                    }
                    letters = Letters.builder()
                            .numberId(number)
                            .letters(newLetters)
                            .build();
                    lettersRepository.save(letters);
                    lastNumber.setNumber(0);
                    lastNumber.setLetters(newLetters);
                    lastNumberRepository.save(lastNumber);
                    return generateResultNumber(number, letters);
                } else {
                    number = Number.builder()
                            .nums(0)
                            .build();
                    numbersRepository.save(number);
                    letters = Letters.builder()
                            .letters(newLetters)
                            .numberId(number)
                            .build();
                    lettersRepository.save(letters);
                    lastNumber.setNumber(0);
                    lastNumber.setLetters(newLetters);
                    lastNumberRepository.save(lastNumber);
                    return generateResultNumber(number, letters);
                }
            }
        } else {
            if (newLetters != null) {
                return increaseNums(0, newLetters, lastNumber);
            } else {
                if (lettersRepository.findById(MAX_SYMBOL_CAPACITY.longValue()).isPresent()) {
                    return "The numbers ran out.";
                } else {
                    return "Choose random";
                }
            }
        }
        System.out.println("L7");
        return null;
    }

    @Override
    public String generateNextNumber() {
        Optional<LastNumber> lastNumberOptional = lastNumberRepository.findById(1L);
        if (lastNumberOptional.isPresent()) {
            LastNumber lastNumber = lastNumberOptional.get();
            Integer lastNums = lastNumber.getNumber();
            String lastLetters = lastNumber.getLetters();
            String resultNumber;
            while (true) {
                if (lastNums < MAX_NUMBER_CAPACITY) {
                    resultNumber = increaseNums(lastNums, lastLetters, lastNumber);
                    if (resultNumber == null) {
                        resultNumber = increaseLetters(lastNums, lastLetters, lastNumber);
                        if (resultNumber == null) {
                            return "The numbers ran out.";
                        } else {
                            return resultNumber;
                        }
                    } else {
                        return resultNumber;
                    }
                } else {
                    resultNumber = increaseLetters(lastNums, lastLetters, lastNumber);
                    if (resultNumber == null) {
                        return "The numbers ran out.";
                    } else {
                        return resultNumber;
                    }
                }
            }
        } else {
            return generateFirstTimeLastNumber();
        }
    }


    private String generateFirstTimeLastNumber() {
        LastNumber lastNumber = LastNumber.builder()
                .id(1L)
                .number(0)
                .letters("ААА")
                .build();
        Number firstNumber = Number.builder()
                .nums(0)
                .build();
        numbersRepository.save(firstNumber);

        Letters firstLetters = Letters.builder()
                .letters("ААА")
                .numberId(firstNumber)
                .build();
        lettersRepository.save(firstLetters);
        lastNumberRepository.save(lastNumber);
        return "А000АА 116RUS";
    }

    private String generateResultNumber(Number number, Letters letters) {
        if (number != null && letters != null) {
            return letters.getLetters().charAt(0) + "" +
                    number.getNums() / 100 + "" + number.getNums() % 100 / 10 + "" + number.getNums() % 100 % 10 + "" +
                    letters.getLetters().substring(1) +
                    " 116RUS";
        } else {
            return "The numbers ran out";
        }
    }
}
