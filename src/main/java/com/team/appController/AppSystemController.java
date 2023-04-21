package com.team.appController;

import com.team.entity.Activity;
import com.team.entity.Facility;
import com.team.mapper.ActicityMapper;
import com.team.mapper.FacilityMapper;
import com.team.service.RentService;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("app")
public class AppSystemController {

    @Resource
    private RentService rentService;
    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private ActicityMapper acticityMapper;

    @PostMapping("user/png")
    public void userHeadPicture(HttpServletRequest request) throws IOException{
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        File file1 = new File("h.png");
        System.out.println(file1.getAbsolutePath());
        for (MultipartFile file: files){
            try{
                byte [] bytes = file.getBytes();
                OutputStream out = new FileOutputStream(file1);
                out.write(bytes);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

