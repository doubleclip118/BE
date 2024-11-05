package PNUMEAT.Backend.global.error.user;


import PNUMEAT.Backend.global.error.ErrorCode;

public class UnauthorizedException extends RuntimeException {
  private final ErrorCode errorCode;

  public UnauthorizedException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  public int getStatusCode(){
    return errorCode.getStatus().value();
  }

}
