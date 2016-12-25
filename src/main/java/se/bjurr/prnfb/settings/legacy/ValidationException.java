package se.bjurr.prnfb.settings.legacy;

@Deprecated
public class ValidationException extends Exception {
  private static final long serialVersionUID = 2203598567281456784L;
  private final String error;
  private final String field;

  public ValidationException() {
    this.error = null;
    this.field = null;
  }

  public ValidationException(String field, String error) {
    this.error = error;
    this.field = field;
  }

  public String getError() {
    return this.error;
  }

  public String getField() {
    return this.field;
  }

  @Override
  public String getMessage() {
    return this.field + "=" + this.error;
  }
}
