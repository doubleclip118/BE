package PNUMEAT.Backend.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import PNUMEAT.Backend.domain.auth.dto.request.MemberProfileCreateRequest;
import PNUMEAT.Backend.domain.auth.dto.request.MemberProfileUpdateRequest;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.Member.MemberNotFoundException;
import PNUMEAT.Backend.global.images.ImageConstant;
import PNUMEAT.Backend.global.images.ImageService;
import java.util.Optional;
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

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 프로필 등록 시 image를 등록하지 않았다면 기본 이미지가 등록된다.")
    void 프로필_등록_시_기본_이미지_등록_테스트(){
        //given
        MemberProfileCreateRequest memberProfileCreateRequest = new MemberProfileCreateRequest("TEST","TEST");
        MultipartFile image = null;
        Member member = new Member("TEST", "TEST", "ROLE_USER");


        //when
        Member memberWithProfile = memberService.createMemberProfile(memberProfileCreateRequest, image, member);

        //then
        assertThat(memberWithProfile.getImageUrl()).isEqualTo(ImageConstant.DEFAULT_MEMBER_PROFILE_IMAGE_URL);
        assertThat(memberWithProfile.getMemberName()).isEqualTo(memberProfileCreateRequest.memberName());
        assertThat(memberWithProfile.getDescription()).isEqualTo(memberProfileCreateRequest.memberExplain());
    }

    @Test
    @DisplayName("회원 프로필 등록 시 image를 등록하면 imageUrl이 변경된다.")
    void 프로필_등록_시_이미지_등록_테스트(){
        //given
        MemberProfileCreateRequest memberProfileCreateRequest = new MemberProfileCreateRequest("TEST","TEST");
        MultipartFile image = new MockMultipartFile("TEST",
                "TEST.png",
                MediaType.IMAGE_PNG_VALUE,
                "TEST".getBytes());
        Member member = new Member("TEST", "TEST", "ROLE_USER");
        given(imageService.profileImageUpload(image)).willReturn("s3://");

        //when
        Member memberWithProfile = memberService.createMemberProfile(memberProfileCreateRequest, image, member);

        //then
        assertThat(memberWithProfile.getImageUrl()).isEqualTo("s3://");
        assertThat(memberWithProfile.getMemberName()).isEqualTo(memberProfileCreateRequest.memberName());
        assertThat(memberWithProfile.getDescription()).isEqualTo(memberProfileCreateRequest.memberExplain());
    }

    @Test
    @DisplayName("uuid를 활용하여 프로필 조회 시 회원 없으면 예외발생")
    void uuid_사용해서_프로필_조회_시_회원_없으면_예외_발생_테스트(){
        //given
        Member member = new Member("TEST.COM", "TEST", "USER_ROLE");
        given(memberRepository.findByUuid(member.getUuid())).willReturn(Optional.empty());

        //expected
        assertThatThrownBy(() -> memberService.getMemberByUUID(member.getUuid()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND_ERROR.getMessage());
    }

    @Test
    @DisplayName("uuid를 활용하여 프로필 정상 조회")
    void uuid_사용해서_프로필_정상_조회(){
        //given
        Member member = new Member("TEST.COM", "TEST", "USER_ROLE");
        given(memberRepository.findByUuid(member.getUuid())).willReturn(Optional.of(member));

        //when
        Member findMember = memberService.getMemberByUUID(member.getUuid());

        //then
        assertThat(findMember.getUuid()).isEqualTo(member.getUuid());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 image를 등록하지 않았다면 이미지 수정 로직이 실행되지 않는다.")
    void 프로필_수정_시_이미지를_바꾸지_않으면_이미지_수정_로직_실행_않는다_테스트(){
        //given
        MemberProfileUpdateRequest memberProfileUpdateRequest = new MemberProfileUpdateRequest("TEST2","TEST2");
        MultipartFile image = null;
        Member member = new Member("TEST", "TEST", "ROLE_USER");

        //when
        Member memberWithProfile = memberService.updateMemberProfile(memberProfileUpdateRequest, image, member);

        //then
        assertThat(memberWithProfile.getMemberName()).isEqualTo(memberProfileUpdateRequest.memberName());
        assertThat(memberWithProfile.getDescription()).isEqualTo(memberProfileUpdateRequest.memberExplain());
        verify(imageService, times(0)).profileImageUpload(any());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 image를 등록하면 이미지 수정 로직이 실행된다.")
    void 프로필_수정_시_이미지를_바꾸면_이미지_수정_로직_실행__테스트(){
        //given
        MemberProfileUpdateRequest memberProfileUpdateRequest = new MemberProfileUpdateRequest("TEST2","TEST2");
        MultipartFile image = new MockMultipartFile("TEST",
                "TEST.png",
                MediaType.IMAGE_PNG_VALUE,
                "TEST".getBytes());
        Member member = new Member("TEST", "TEST", "ROLE_USER");
        given(imageService.profileImageUpload(image)).willReturn("s3://");

        //when
        Member memberWithProfile = memberService.updateMemberProfile(memberProfileUpdateRequest, image, member);

        //then
        assertThat(memberWithProfile.getMemberName()).isEqualTo(memberProfileUpdateRequest.memberName());
        assertThat(memberWithProfile.getDescription()).isEqualTo(memberProfileUpdateRequest.memberExplain());
        verify(imageService, times(1)).profileImageUpload(any());
    }

}