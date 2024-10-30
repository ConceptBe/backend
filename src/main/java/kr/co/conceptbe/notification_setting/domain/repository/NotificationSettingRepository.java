package kr.co.conceptbe.notification_setting.domain.repository;

import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<IdeaNotificationSetting, Long> {
    IdeaNotificationSetting findByMemberId(Long memberId);
}
