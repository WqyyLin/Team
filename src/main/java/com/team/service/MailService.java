package com.team.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfWriter;
import com.team.entity.Rent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        File fileName = new File("./picture/"+rent.getOrderNumber()+ ".pdf");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        if (!fileName.exists()){
        if (fileName.exists()){
            try (OutputStream file = new FileOutputStream(fileName)) {
                Rectangle pageSize = new Rectangle(560f, 360f);
                Document document = new Document(pageSize, 36f, 36f, 72f, 72f);
                PdfWriter.getInstance(document, file);
                document.open();
                Font font = new Font();
                font.setColor(243, 177, 137);
                font.setSize(26);
                Paragraph paragraph = new Paragraph();
                paragraph.setFont(font);
                paragraph.setSpacingAfter(30);
                paragraph.add("                       Order invoice");
                document.add(paragraph);
                Paragraph user = new Paragraph();
                user.setSpacingBefore(10);
                user.add("Reserved user:\t" + rent.getEmail());
                document.add(user);
                Paragraph order = new Paragraph();
                order.setSpacingBefore(10);
                order.add("Order number:\t" + rent.getNum());
                document.add(order);
                Paragraph rentTime = new Paragraph();
                rentTime.setSpacingBefore(10);
                rentTime.add("Appointment time:" + df.format(rent.getRentTime()));
                document.add(rentTime);
                Paragraph price = new Paragraph();
                price.setSpacingBefore(10);
                price.add("Reservation price:\t" + rent.getMoney());
                document.add(price);
                BarcodeQRCode barcodeQRCode = new BarcodeQRCode("http://localhost:8080/app/user/code/"+"", 32, 32, null);
                document.add(barcodeQRCode.getImage());
                // 关闭document
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        try {
//            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
//            message.addAttachment("order.pdf", fileName);
//            // 设置邮件主题
//            message.setSubject("Order");
//            // 设置邮件发送者
//            message.setFrom(mailUsername);
//            // 设置邮件接收者，可以多个
//            message.setTo(email);
//            // 设置邮件抄送人，可以多个
//            // message.setCc();
//            // 设置隐秘抄送人， 可以多个
//            // message.setBcc();
//            // 设置邮件发送日期
//            message.setSentDate(new Date());
//            String text = "Reserved user:" + rent.getEmail() + "\n" +
//                    "Reservation number:" + rent.getNum() + "\n" +
//                    "Facility:" + rent.getFacility() + "\n" +
//                    "Reservation price:" + rent.getMoney() + "\n" +
//                    "Appointment time:" + df.format(rent.getRentTime() )+ "\n" +
//                    "order:" + rent.getOrderNumber() + "\n";
//            // 设置邮件正文
//            message.setText(text, false);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        javaMailSender.send(mimeMessage);
    }

}
