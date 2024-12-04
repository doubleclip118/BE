package PNUMEAT.Backend.domain.auth.service;

import PNUMEAT.Backend.domain.auth.dto.request.MemberProfileCreateRequest;
import PNUMEAT.Backend.domain.auth.dto.request.MemberProfileUpdateRequest;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.global.error.Member.MemberNotFoundException;
import PNUMEAT.Backend.global.images.ImageConstant;
import PNUMEAT.Backend.global.images.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Transactional
    public Member createMemberProfile(MemberProfileCreateRequest memberProfileCreateRequest,
                                      MultipartFile image,
                                      Member member){
        member.updateProfile(memberProfileCreateRequest.memberName(), memberProfileCreateRequest.memberExplain());

        if (image != null){
            String imageUrl = imageService.profileImageUpload(image);
            member.updateImageUrl(imageUrl);
        }

        return member;
    }

    @Transactional
    public Member updateMemberProfile(MemberProfileUpdateRequest memberProfileUpdateRequest,
                                      MultipartFile image,
                                      Member member){

        member.updateProfile(memberProfileUpdateRequest.memberName(), memberProfileUpdateRequest.memberExplain());

        if (image != null){
            String imageUrl = imageService.profileImageUpload(image);
            member.updateImageUrl(imageUrl);
        }

        return member;
    }

    public Member getMemberByUUID(String uuid){
        return memberRepository.findByUuid(uuid)
                .orElseThrow(MemberNotFoundException::new);
    }
}
