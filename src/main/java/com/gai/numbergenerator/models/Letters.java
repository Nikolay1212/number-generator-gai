package com.gai.numbergenerator.models;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "numberId")
@Entity
public class Letters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String letters;

    @ManyToOne
    @JoinColumn(name = "number_id")
    private Number numberId;
}
