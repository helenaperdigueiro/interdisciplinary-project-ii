package com.digitalmoneyhouse.iamservice.mail;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}