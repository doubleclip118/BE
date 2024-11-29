package PNUMEAT.Backend.global.error.images;


import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.Team24Exception;

public class ImageFileUploadException extends Team24Exception {
    public ImageFileUploadException() {
        super(ErrorCode.IMAGE_FILE_UPLOAD_ERROR);
    }

}
