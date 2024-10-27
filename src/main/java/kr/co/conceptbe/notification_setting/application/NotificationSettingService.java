package kr.co.conceptbe.notification_setting.application;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.stream.Collectors;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.branch.domain.persistense.BranchRepository;
import kr.co.conceptbe.common.auth.Auth;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.idea.domain.vo.CooperationWay;
import kr.co.conceptbe.member.exception.UnAuthorizedMemberException;
import kr.co.conceptbe.notification_setting.application.dto.NotificationSettingInfoResponse;
import kr.co.conceptbe.notification_setting.application.dto.NotificationSettingMemberInfoResponse;
import kr.co.conceptbe.notification_setting.application.dto.NotificationSettingRequest;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import kr.co.conceptbe.notification_setting.domain.repository.NotificationSettingRepository;
import kr.co.conceptbe.purpose.domain.Purpose;
import kr.co.conceptbe.purpose.domain.persistence.PurposeRepository;
import kr.co.conceptbe.skill.domain.SkillCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final PurposeRepository purposeRepository;
    private final BranchRepository branchRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    public NotificationSettingInfoResponse getNotificationSettingInfo() {
        return NotificationSettingInfoResponse.of(
                purposeRepository.findAll(),
                CooperationWay.values(),
                branchRepository.findAll()
        );
    }

    public NotificationSettingMemberInfoResponse getNotificationSettingMemberInfo(AuthCredentials auth) {
        IdeaNotificationSetting ideaNotificationSetting =  notificationSettingRepository.findByMemberId(auth.id());
        return NotificationSettingMemberInfoResponse.from(ideaNotificationSetting);
    }

    @Transactional
    public Long createNotificationSetting(AuthCredentials auth, NotificationSettingRequest notificationSettingRequest) {
        IdeaNotificationSetting ideaNotificationSetting = IdeaNotificationSetting.of(
                auth.id(),
                new HashSet<>(branchRepository.findByIdIn(notificationSettingRequest.branchIds())),
                new HashSet<>(purposeRepository.findByIdIn(notificationSettingRequest.purposeIds())),
                notificationSettingRequest.cooperationWay()
        );

        IdeaNotificationSetting savedIdeaNotificationSetting = notificationSettingRepository.save(ideaNotificationSetting);
        return savedIdeaNotificationSetting.getId();
    }


    @Transactional
    public void updateNotificationSetting(
            AuthCredentials auth,
            Long memberId,
            NotificationSettingRequest notificationSettingRequest
    ) {
        validateMember(auth, memberId);

        IdeaNotificationSetting ideaNotificationSetting =  notificationSettingRepository.findByMemberId(memberId);
        ideaNotificationSetting.update(
                new HashSet<>(branchRepository.findByIdIn(notificationSettingRequest.branchIds())),
                new HashSet<>(purposeRepository.findByIdIn(notificationSettingRequest.purposeIds())),
                notificationSettingRequest.cooperationWay()
        );
    }

    private void validateMember(AuthCredentials auth, Long memberId) {
        if (!auth.id().equals(memberId)) {
            throw new UnAuthorizedMemberException();
        }
    }
}
