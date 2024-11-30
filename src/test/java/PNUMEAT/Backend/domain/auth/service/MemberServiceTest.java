package PNUMEAT.Backend.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import PNUMEAT.Backend.domain.auth.dto.request.MemberProfileRequest;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.global.images.ImageConstant;
import PNUMEAT.Backend.global.images.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 프로필 등록 시 image를 등록하지 않았다면 기본 이미지가 등록된다.")
    void 프로필_등록_시_기본_이미지_등록_테스트(){
        //given
        MemberProfileRequest memberProfileRequest = new MemberProfileRequest("TEST","TEST");
        MultipartFile image = null;
        Member member = new Member("TEST", "TEST", "ROLE_USER");


        //when
        Member memberWithProfile = memberService.createMemberProfile(memberProfileRequest, image, member);

        //then
        assertThat(memberWithProfile.getImageUrl()).isEqualTo(ImageConstant.DEFAULT_MEMBER_PROFILE_URL);
        assertThat(memberWithProfile.getMemberName()).isEqualTo(memberProfileRequest.memberName());
        assertThat(memberWithProfile.getDescription()).isEqualTo(memberProfileRequest.memberExplain());
    }

    @Test
    @DisplayName("회원 프로필 등록 시 image를 등록하면 imageUrl이 변경된다.")
    void 프로필_등록_시_이미지_등록_테스트(){
        //given
        MemberProfileRequest memberProfileRequest = new MemberProfileRequest("TEST","TEST");
        MultipartFile image = new MockMultipartFile("TEST",
                "TEST.png",
                MediaType.IMAGE_PNG_VALUE,
                "TEST".getBytes());
        Member member = new Member("TEST", "TEST", "ROLE_USER");
        given(imageService.profileImageUpload(image)).willReturn("s3://");

        //when
        Member memberWithProfile = memberService.createMemberProfile(memberProfileRequest, image, member);

        //then
        assertThat(memberWithProfile.getImageUrl()).isEqualTo("s3://");
        assertThat(memberWithProfile.getMemberName()).isEqualTo(memberProfileRequest.memberName());
        assertThat(memberWithProfile.getDescription()).isEqualTo(memberProfileRequest.memberExplain());
    }
}