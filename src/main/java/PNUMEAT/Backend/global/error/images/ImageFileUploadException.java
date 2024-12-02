package PNUMEAT.Backend.global.error.images;


import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.ComonException;

public class ImageFileUploadException extends ComonException {
    public ImageFileUploadException() {
        super(ErrorCode.IMAGE_FILE_UPLOAD_ERROR);
    }

}
