package PNUMEAT.Backend.images;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// 예시 컨트롤러임 쓰지마
@RestController
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/api/image/upload")
    public Map<String, Object> imageUpload(@RequestParam("upload") MultipartFile multipartFile)  {

        Map<String, Object> responseData = new HashMap<>();


        String s3Url = imageService.imageload(multipartFile,1L);

        responseData.put("uploaded",true);
        responseData.put("url",s3Url);

        return responseData;
    }

}

