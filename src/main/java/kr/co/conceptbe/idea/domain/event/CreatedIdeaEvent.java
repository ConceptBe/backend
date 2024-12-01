package kr.co.conceptbe.idea.domain.event;

import kr.co.conceptbe.idea.domain.Idea;

public record CreatedIdeaEvent(
        Idea idea
) {
}
