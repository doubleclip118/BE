package PNUMEAT.Backend.global.error.images;


import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.ComonException;

public class S3Exception extends ComonException {
    public S3Exception() {
        super(ErrorCode.S3_NETWORK_ERROR);
    }

}
