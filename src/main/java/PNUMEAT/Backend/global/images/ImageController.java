//package PNUMEAT.Backend.global.images;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//
//// 예시 컨트롤러임 쓰지마
//@Controller
//public class ImageController {
//
//    private final ImageService imageService;
//
//    public ImageController(ImageService imageService) {
//        this.imageService = imageService;
//    }
//
//    @PostMapping("/api/image/upload")
//    @ResponseBody
//    public Map<String, Object> imageUpload(@RequestParam("upload") MultipartFile multipartFile)  {
//
//        Map<String, Object> responseData = new HashMap<>();
//
//
//        String s3Url = imageService.imageload(multipartFile,1L);
//
//        responseData.put("uploaded",true);
//        responseData.put("url",s3Url);
//
//        return responseData;
//    }
//
//    @GetMapping("/image/upload")
//    public String showImageUploadPage(Model model) {
//        // 모델을 사용해 추가 데이터를 전달할 수 있습니다.
//        return "imageUpload"; // 타임리프 템플릿 이름 (image.html)
//    }
//
//}
//
