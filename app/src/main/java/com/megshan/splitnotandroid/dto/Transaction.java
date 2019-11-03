package com.megshan.splitnotandroid.dto;

import java.util.List;

import lombok.Data;

@Data
public class Transaction {

    private String accountId;
    private Double amount;
    private String isoCurrencyCode;
    private String unofficialCurrencyCode;
    private List<String> category;
    private String categoryId;
    private String date;
    private Transaction.Location location;
    private String name;
    private String originalDescription;
    private Transaction.PaymentMeta paymentMeta;
    private Boolean pending;
    private String pendingTransactionId;
    private String transactionId;
    private String transactionType;
    private String accountOwner;

    @Data
    public static final class Location {
        private String address;
        private String city;
        private String state;
        private String zip;
        private Double lat;
        private Double lon;
        private String storeNumber;
    }

    @Data
    public static final class PaymentMeta {
        private String byOrderOf;
        private String payee;
        private String payer;
        private String paymentMethod;
        private String paymentProcessor;
        private String ppdId;
        private String reason;
        private String referenceNumber;
    }
}
