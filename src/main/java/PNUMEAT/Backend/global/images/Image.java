package PNUMEAT.Backend.global.images;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Getter
public class Image {
    private final String originalFileName;
    private final String uniqueFileName;
    private final String localPath;
    private final File localFile;

    public Image(MultipartFile multipartFile, Long id, String localLocation) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            String defaultImageFilePath = "/Users/pakjeongwoo/4-2/pnu_meat/BE/default-image.jpg";
            File defaultImageFile = new File(defaultImageFilePath);

            if (!defaultImageFile.exists()) {
                throw new RuntimeException("기본 이미지 파일이 존재하지 않습니다: " + defaultImageFilePath);
            }

            this.originalFileName = defaultImageFile.getName();
            this.uniqueFileName = "default-image-" + id + ".jpg";
            this.localPath = defaultImageFilePath;
            this.localFile = defaultImageFile;
        } else {
            this.originalFileName = multipartFile.getOriginalFilename();
            String ext = extractExtension(originalFileName);
            this.uniqueFileName = id + ext;
            this.localPath = localLocation + uniqueFileName;
            this.localFile = new File(localPath);

            try {
                multipartFile.transferTo(localFile);
            } catch (IOException e) {
                System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
                throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
            }
        }
    }

    private String extractExtension(String originalFileName) {
        if (originalFileName != null && originalFileName.contains(".")) {
            return originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return "";
    }

    public String uploadToS3(S3Client s3Client, String bucket, String region) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(this.uniqueFileName)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(this.localFile));

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, this.uniqueFileName);
    }

    public boolean deleteLocalFile() {
        return localFile.delete();
    }
}
