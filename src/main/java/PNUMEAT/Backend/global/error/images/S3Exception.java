package PNUMEAT.Backend.global.error.images;


import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.Team24Exception;

public class S3Exception extends Team24Exception {
  public S3Exception() {
    super(ErrorCode.S3_NETWORK_ERROR);
  }

}
