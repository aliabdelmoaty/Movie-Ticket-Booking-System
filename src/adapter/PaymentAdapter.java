package adapter;

/**
 * Adapter Pattern: PaymentAdapter
 * Adapts different payment systems to work with the booking system
 * Provides a unified interface for various payment methods
 * 
 * Note: PaymentProcessor interface moved to PaymentProcessor.java
 */

// Adaptee 1: Credit Card Payment System
class CreditCardPaymentSystem {
    private String transactionId;
    
    public boolean chargeCreditCard(String cardNumber, String cvv, double amount) {
        // Simulate credit card processing
        System.out.println("Processing credit card payment: $" + amount);
        this.transactionId = "CC-" + System.currentTimeMillis();
        return true;
    }
    
    public String getLastTransactionId() {
        return transactionId;
    }
}

// Adaptee 2: PayPal Payment System
class PayPalPaymentSystem {
    private String orderId;
    
    public boolean makePayment(String email, double totalAmount) {
        // Simulate PayPal processing
        System.out.println("Processing PayPal payment: $" + totalAmount);
        this.orderId = "PP-" + System.currentTimeMillis();
        return true;
    }
    
    public String getOrderId() {
        return orderId;
    }
}

// Adaptee 3: Bank Transfer System
class BankTransferSystem {
    private String referenceNumber;
    
    public boolean transferFunds(String accountNumber, double funds) {
        // Simulate bank transfer
        System.out.println("Processing bank transfer: $" + funds);
        this.referenceNumber = "BT-" + System.currentTimeMillis();
        return true;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
}

// Adapter 1: Credit Card Adapter
class CreditCardAdapter implements PaymentProcessor {
    private CreditCardPaymentSystem creditCardSystem;
    private boolean paymentSuccessful;
    
    public CreditCardAdapter() {
        this.creditCardSystem = new CreditCardPaymentSystem();
    }
    
    @Override
    public boolean processPayment(double amount, String customerInfo) {
        // Parse customer info to extract card details
        // In real scenario, this would be properly encrypted and validated
        String[] parts = customerInfo.split(",");
        String cardNumber = parts.length > 0 ? parts[0] : "XXXX";
        String cvv = parts.length > 1 ? parts[1] : "XXX";
        
        paymentSuccessful = creditCardSystem.chargeCreditCard(cardNumber, cvv, amount);
        return paymentSuccessful;
    }
    
    @Override
    public String getPaymentStatus() {
        return paymentSuccessful ? "Payment Successful via Credit Card" : "Payment Failed";
    }
    
    @Override
    public String getTransactionId() {
        return creditCardSystem.getLastTransactionId();
    }
}

// Adapter 2: PayPal Adapter
class PayPalAdapter implements PaymentProcessor {
    private PayPalPaymentSystem paypalSystem;
    private boolean paymentSuccessful;
    
    public PayPalAdapter() {
        this.paypalSystem = new PayPalPaymentSystem();
    }
    
    @Override
    public boolean processPayment(double amount, String customerInfo) {
        // customerInfo should be email for PayPal
        paymentSuccessful = paypalSystem.makePayment(customerInfo, amount);
        return paymentSuccessful;
    }
    
    @Override
    public String getPaymentStatus() {
        return paymentSuccessful ? "Payment Successful via PayPal" : "Payment Failed";
    }
    
    @Override
    public String getTransactionId() {
        return paypalSystem.getOrderId();
    }
}

// Adapter 3: Bank Transfer Adapter
class BankTransferAdapter implements PaymentProcessor {
    private BankTransferSystem bankSystem;
    private boolean paymentSuccessful;
    
    public BankTransferAdapter() {
        this.bankSystem = new BankTransferSystem();
    }
    
    @Override
    public boolean processPayment(double amount, String customerInfo) {
        // customerInfo should be account number for bank transfer
        paymentSuccessful = bankSystem.transferFunds(customerInfo, amount);
        return paymentSuccessful;
    }
    
    @Override
    public String getPaymentStatus() {
        return paymentSuccessful ? "Payment Successful via Bank Transfer" : "Payment Failed";
    }
    
    @Override
    public String getTransactionId() {
        return bankSystem.getReferenceNumber();
    }
}

// Note: PaymentAdapterFactory moved to PaymentAdapterFactory.java
