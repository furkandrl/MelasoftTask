package com.dereli.melasoft_task.model;

public class InvoiceModel {
    private final String invoiceNumber;
    private final String sellerCountryCode;
    private final String sellerVatNumber;
    private final String buyerCountryCode;
    private final String buyerVatNumber;
    private final String totalAmount;
    private final String currency;

    public InvoiceModel(String invoiceNumber,
                   String sellerCountryCode, String sellerVatNumber,
                   String buyerCountryCode, String buyerVatNumber) {
        this(invoiceNumber, sellerCountryCode, sellerVatNumber, buyerCountryCode, buyerVatNumber, null, null);
    }

    public InvoiceModel(String invoiceNumber,
                   String sellerCountryCode, String sellerVatNumber,
                   String buyerCountryCode, String buyerVatNumber,
                   String totalAmount, String currency) {
        this.invoiceNumber = invoiceNumber;
        this.sellerCountryCode = sellerCountryCode;
        this.sellerVatNumber = sellerVatNumber;
        this.buyerCountryCode = buyerCountryCode;
        this.buyerVatNumber = buyerVatNumber;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public String getSellerCountryCode() { return sellerCountryCode; }
    public String getSellerVatNumber() { return sellerVatNumber; }
    public String getBuyerCountryCode() { return buyerCountryCode; }
    public String getBuyerVatNumber() { return buyerVatNumber; }
    public String getTotalAmount() { return totalAmount; }
    public String getCurrency() { return currency; }
}
