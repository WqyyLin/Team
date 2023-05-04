package com.team.service;

import com.team.entity.Rent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String mailUsername;
    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 激活账号邮件发送
     * @param activationUrl
     * @param email
     */
    public void sendMailForActivationAccount(String activationUrl, String email) {
        // 创建邮件对象
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
            // 设置邮件主题
            message.setSubject("Welcome to the Reservation System - Personal Account activation");
            // 设置邮件发送者
            message.setFrom(mailUsername);
            // 设置邮件接收者，可以多个
            message.setTo(email);
            // 设置邮件抄送人，可以多个
            // message.setCc();
            // 设置隐秘抄送人， 可以多个
            // message.setBcc();
            // 设置邮件发送日期
            message.setSentDate(new Date());
            String text = "If you are a new user, just click the link below to activate your account:" + activationUrl;
            // 设置邮件正文
            message.setText(text, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }

    public void sendMailForOrder(Rent rent, String email) {
        // 创建邮件对象
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
            // 设置邮件主题
            message.setSubject("Order");
            // 设置邮件发送者
            message.setFrom(mailUsername);
            // 设置邮件接收者，可以多个
            message.setTo(email);
            // 设置邮件抄送人，可以多个
            // message.setCc();
            // 设置隐秘抄送人， 可以多个
            // message.setBcc();
            // 设置邮件发送日期
            message.setSentDate(new Date());
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String text = "Reserved user:" + rent.getEmail() + "\n" +
                    "Reservation number:" + rent.getNum() + "\n" +
                    "Facility:" + rent.getFacility() + "\n" +
                    "Reservation price:" + rent.getMoney() + "\n" +
                    "Appointment time:" + df.format(rent.getRentTime() )+ "\n" +
                    "order:" + rent.getOrderNumber() + "\n";
            // 设置邮件正文
            message.setText(text, false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }

}
