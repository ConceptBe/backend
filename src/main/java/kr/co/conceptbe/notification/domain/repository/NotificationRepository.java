package kr.co.conceptbe.notification.domain.repository;

import kr.co.conceptbe.notification.domain.IdeaNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<IdeaNotification, Long> {

    @Query(value = """
            SELECT *
            FROM idea_notification
            WHERE member_id = :memberId and id < :cursorId
            LIMIT :limit
            """, nativeQuery = true)
    List<IdeaNotification> findAllNotifications(Long memberId, Long cursorId, Long limit);

}
