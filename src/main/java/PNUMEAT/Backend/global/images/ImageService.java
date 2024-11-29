package PNUMEAT.Backend.global.images;

import PNUMEAT.Backend.global.error.images.ImageFileDeleteException;
import PNUMEAT.Backend.global.error.images.ImageFileUploadException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@Slf4j
public class ImageService {
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    private static final String LOCAL_LOCATION = "/Users/pakjeongwoo/Downloads/";
    public ImageService(S3Client s3Client, AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    public String imageUpload(MultipartFile multipartFile) {
        String s3Url;
        try {
            String uuid = UUID.randomUUID().toString();
            Image image = new Image(multipartFile, uuid, LOCAL_LOCATION);
            s3Url = image.uploadToS3(s3Client, awsProperties.getBucket(), awsProperties.getRegion());

            if (!image.deleteLocalFile()) {
                log.error("로컬 파일 삭제 실패: {}", image.getLocalPath());
            }
        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생: {}", e.getMessage());
            throw new ImageFileUploadException();
        }

        return s3Url;
    }

    public String imageUpdate(MultipartFile multipartFile, String imageUrl) {
        String updatedS3Url;
        try {
            String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Image image = new Image(multipartFile, key, LOCAL_LOCATION);
            updatedS3Url = image.uploadToS3(s3Client, awsProperties.getBucket(), awsProperties.getRegion());

            if (!image.deleteLocalFile()) {
                log.error("로컬 파일 삭제 실패: {}", image.getLocalPath());
            }
        } catch (Exception e) {
            log.error("이미지 업데이트 중 오류 발생: {}", e.getMessage());
            throw new ImageFileUploadException();
        }

        return updatedS3Url;
    }

    public void deleteImageByUrl(String s3Url) {
        try {
            String key = s3Url.substring(s3Url.lastIndexOf("/") + 1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("이미지가 성공적으로 삭제되었습니다: {}", key);
        } catch (Exception e) {
            log.error("이미지 삭제 중 오류 발생: {}", e.getMessage());
            throw new ImageFileDeleteException();
        }
    }
}
