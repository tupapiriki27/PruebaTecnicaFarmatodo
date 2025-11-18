package co.com.testapp.testapp.exception;

public class PaymentFailedException extends RuntimeException {

  public PaymentFailedException(String message) {
    super(message);
  }

}

