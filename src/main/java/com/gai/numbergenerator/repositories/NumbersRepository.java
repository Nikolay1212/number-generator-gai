package com.gai.numbergenerator.repositories;

import com.gai.numbergenerator.models.Number;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NumbersRepository extends CrudRepository<Number, Long> {

    Optional<Number> findByNums(Integer nums);

    @Query(value = "SELECT max(id) - min(id) + 1 from Number")
    Long numberCount();

    @Query("SELECT n FROM Number n where n.lettersList.size < 1000")
    List<Number> getNumbersWithFreeLettersSpace();
}
