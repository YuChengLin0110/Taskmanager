package com.example.taskmanager.notification.strategy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.notification.strategy.NotificationStrategy;

@Component
public class EmailNotificationStrategy implements NotificationStrategy {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationStrategy.class);

	private final JavaMailSender mailSender;

	@Autowired
	public EmailNotificationStrategy(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public NotificationEnum getChannel() {
		return NotificationEnum.EMAIL;
	}

	@Override
	public void send(NotificationRequest request) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom("noreply@example.com");
			mailMessage.setTo(request.getTo());
			mailMessage.setSubject(request.getSubject());
			mailMessage.setText(request.getMessage());

			mailSender.send(mailMessage);
			
			log.info("Email sent success");
		} catch (Exception e) {
			log.error("Failed to send email ", e);
		}
	}
}