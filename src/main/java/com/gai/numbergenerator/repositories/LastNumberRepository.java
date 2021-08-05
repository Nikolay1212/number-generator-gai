package com.gai.numbergenerator.repositories;

import com.gai.numbergenerator.models.LastNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastNumberRepository extends JpaRepository<LastNumber, Long> {
}
