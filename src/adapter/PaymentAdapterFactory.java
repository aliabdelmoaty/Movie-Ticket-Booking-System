package adapter;

/**
 * Factory to create appropriate payment adapter
 */
public class PaymentAdapterFactory {
    public enum PaymentMethod {
        CREDIT_CARD, PAYPAL, BANK_TRANSFER
    }
    
    public static PaymentProcessor createPaymentProcessor(PaymentMethod method) {
        switch (method) {
            case CREDIT_CARD:
                return new CreditCardAdapter();
            case PAYPAL:
                return new PayPalAdapter();
            case BANK_TRANSFER:
                return new BankTransferAdapter();
            default:
                return new CreditCardAdapter();
        }
    }
}
