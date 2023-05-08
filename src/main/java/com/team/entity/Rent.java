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
    private Integer pid;
    private Integer isLesson;
    private String email;
    private LocalDateTime time;
    private LocalDateTime rentTime;
    private Integer money;
    private String facility;
    private LocalDateTime limitTime;
    private Integer num;
    private Integer used;
    private String orderNumber;
    private Integer peopleNum;
    private String validTime;

}
