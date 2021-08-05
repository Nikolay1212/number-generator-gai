package com.gai.numbergenerator.repositories;

import com.gai.numbergenerator.models.Letters;
import com.gai.numbergenerator.models.Number;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LettersRepository extends CrudRepository<Letters, Long> {
    Optional<Letters> findByLetters(String letters);
    List<Letters> findLettersByNumberId(Number number);
}
