package com.team.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rent {

    private Integer rid;
    private String facility;
    private String activity;
    private String email;
    private LocalDateTime time;
    private LocalDateTime rentTime;
    private Integer money;
    private LocalDateTime limitTime;

}
