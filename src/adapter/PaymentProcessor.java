package adapter;

/**
 * Adapter Pattern: PaymentProcessor Interface
 * Target interface that our system expects for payment processing
 */
public interface PaymentProcessor {
    boolean processPayment(double amount, String customerInfo);
    String getPaymentStatus();
    String getTransactionId();
}
