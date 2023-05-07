package com.team.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facility {

    private Integer fid;
    private String name;
    private Integer capacity;
    private Integer facilitiesNumber;
    private LocalDateTime stopTime;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String description;
    private Integer isValid;
    private Integer num;
    private String picture;
}
