package PNUMEAT.Backend.domain.auth.service;

import PNUMEAT.Backend.domain.auth.dto.request.MemberProfileRequest;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.global.images.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final ImageService imageService;
    @Transactional
    public Member createMemberProfile(MemberProfileRequest memberProfileRequest,
                                    MultipartFile image,
                                    Member member){
        member.updateProfile(memberProfileRequest.memberName(),memberProfileRequest.memberExplain());

        if (image != null){
            String imageUrl = imageService.profileImageUpload(image);
            member.updateImageUrl(imageUrl);
        }

        return member;
    }

}
