//package PNUMEAT.Backend.global.images;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ImageTest {
//
//    @Mock
//    private MultipartFile multipartFile;
//
//    private final String localLocation = "/tmp/";
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("MultipartFile이 있는 경우 이미지 객체 생성 테스트")
//    void 이미지_생성_성공() throws IOException {
//        when(multipartFile.isEmpty()).thenReturn(false);
//        when(multipartFile.getOriginalFilename()).thenReturn("test-image.jpg");
//        when(multipartFile.getInputStream()).thenReturn(new FileInputStream(new File("src/test/resources/test-image.jpg")));
//
//        Image image = new Image(multipartFile, "unique-id", localLocation);
//
//        assertNotNull(image);
//        assertEquals("test-image.jpg", image.getOriginalFileName());
//        assertTrue(image.getUniqueFileName().contains("unique-id"));
//    }
//}
