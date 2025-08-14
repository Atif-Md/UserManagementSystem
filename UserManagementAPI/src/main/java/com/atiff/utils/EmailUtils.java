package com.atiff.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public boolean sendEmailMsg(String toMail, String subject, String body) {
		boolean mailSentStatus = false;
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setTo(toMail);
			helper.setSentDate(new Date());
			helper.setText(body, true);
			helper.setSubject(subject);
			mailSender.send(msg);
			mailSentStatus = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mailSentStatus;
	}

}
