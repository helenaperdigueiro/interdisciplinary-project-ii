package com.digitalmoneyhouse.accountservice.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public  static String formatBigDecimal(BigDecimal value) {
        return String.format(Locale.GERMAN, "%,.2f", value);
    }

    public static String formatDateInDayMonthYear(LocalDate date) {
        String pattern = "dd/MM/yyyy";
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDateInDayMonthYear(LocalDate date, String separator) {
        String pattern = "dd/MM/yyyy";
        if (separator != null) {
            pattern = pattern.replace("/", separator);
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime toBrasiliaTime(LocalDateTime date) {
        ZoneId systemZoneId = ZoneId.systemDefault();
        ZonedDateTime zonedSystemDateTime = ZonedDateTime.of(date, systemZoneId);
        ZoneId targetZoneId = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime zonedTargetDateTime = zonedSystemDateTime.withZoneSameInstant(targetZoneId);
        return zonedTargetDateTime.toLocalDateTime();
    }

    public static String formatCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
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
