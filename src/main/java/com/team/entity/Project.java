package com.team.entity;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private Integer pid;
    private String name;
    private String facility;
    private String activity;
    private Integer money;
    private String weekDay;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private Integer isWeekly;
    private Integer capacity;
    private Integer isLesson;
    private Integer valid;
}
