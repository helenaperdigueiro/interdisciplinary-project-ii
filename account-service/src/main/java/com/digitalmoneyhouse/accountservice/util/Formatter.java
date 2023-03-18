package com.digitalmoneyhouse.accountservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatter {

    public static String formatDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy 'às' HH'h'mm", Locale.forLanguageTag("pt-BR"));
        String formattedDateTime = date.format(formatter);
        return formattedDateTime;
    }

    public  static String formatDouble(Double value) {
        return String.format(Locale.GERMAN, "%,.2f", value);
    }

    public static String maskCardNumber(String cardNumber) {
        StringBuilder maskedNumber = new StringBuilder();
        String firstFourDigits = cardNumber.substring(0, 4);
        String fourthAndFifthDigits = cardNumber.substring(4, 6);
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        maskedNumber.append(firstFourDigits + " ");
        maskedNumber.append(fourthAndFifthDigits);
        maskedNumber.append(" ** **** ");
        maskedNumber.append(lastFourDigits);
        return maskedNumber.toString();
    }
}
