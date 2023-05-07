package com.team.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    private Integer aid;
    private String name;
    private String facility;
    private Integer isLesson;
    private Integer money;
    private String description;
    private String picture;

}
