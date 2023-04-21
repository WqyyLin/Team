package com.team.component;

import com.team.service.RentService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
//开启定时任务
@EnableScheduling
public class DatabaseTask {

    @Resource
    private RentService rentService;

    // cron 格式：[秒][分][小时][日][月][周][年](可以为空)
    // 每1天执行一次
    @Scheduled(cron = "0 0 0 * * ? ")
    private void rent30AgoDelete() {
        rentService.rent30AgoDelete();
    }

    //每小时执行一次
    @Scheduled(cron = "0 0 * * * ?")
    private void stopRentRestart(){
        rentService.stopRentRestart();
    }

}
