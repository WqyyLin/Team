package com.team.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private Integer cid;
    private String name;
    private Integer type;
    private Integer money;
    private Integer time;
    private Integer discount;
    private Integer valid;
}
