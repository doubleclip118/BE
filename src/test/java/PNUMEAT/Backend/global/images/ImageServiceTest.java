package PNUMEAT.Backend.global.images;

import PNUMEAT.Backend.global.error.images.ImageFileDeleteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ImageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private AwsProperties awsProperties;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageService imageService;

    private final String localLocation = "/tmp/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(awsProperties.getBucket()).thenReturn("test-bucket");
        when(awsProperties.getRegion()).thenReturn("test-region");
    }

    @Test
    @DisplayName("이미지 업로드 성공 테스트")
    void 이미지_업로드_성공() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test-image.jpg");
        when(multipartFile.getInputStream()).thenReturn(new FileInputStream(new File("src/test/resources/test-image.jpg")));

        doReturn(null).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        String s3Url = imageService.imageload(multipartFile, 1L);

        assertNotNull(s3Url);
        assertTrue(s3Url.contains("test-bucket"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("MultipartFile이 비어 있을 때 기본 이미지 업로드 테스트")
    void 기본이미지_업로드_성공() {
        when(multipartFile.isEmpty()).thenReturn(true);

        doReturn(null).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        String s3Url = imageService.imageload(multipartFile, 1L);

        assertNotNull(s3Url);
        assertTrue(s3Url.contains("default-image"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("이미지 삭제 성공 테스트")
    void 이미지_삭제_성공() {
        String s3Url = "https://test-bucket.s3.test-region.amazonaws.com/test-image.jpg";

        doReturn(null).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        imageService.deleteImageByUrl(s3Url);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("이미지 삭제 실패 테스트")
    void 이미지_삭제_실패() {
        String s3Url = "https://test-bucket.s3.test-region.amazonaws.com/test-image.jpg";

        doThrow(new RuntimeException("Deletion failed")).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        ImageFileDeleteException exception = assertThrows(ImageFileDeleteException.class, () -> {
            imageService.deleteImageByUrl(s3Url);
        });

        assertNotNull(exception);
    }
}
