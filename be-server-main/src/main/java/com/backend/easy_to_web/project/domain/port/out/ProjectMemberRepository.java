package com.backend.easy_to_web.project.domain.port.out;


import com.backend.easy_to_web.project.domain.model.Member;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository {

  List<Member> findAllByAccountId(UUID accountId);

  Optional<Member> findByAccountIdAndProjectId(UUID accountId, UUID projectId);

  Member getByAccountIdAndProjectId(UUID accountId, UUID projectId);

  Member save(Member member);

  void delete(Member member);

}
