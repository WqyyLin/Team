package com.team;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.team.mapper")
@SpringBootApplication
public class TeamworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamworkApplication.class, args);
    }

}
