package com.backend.easy_to_web.project.domain.model;

import com.backend.easy_to_web.common.domain.model.Domain;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Publish extends Domain {

	private UUID projectId;
	private String url;
	private String content;
	private int viewCount;


	@Builder
	public Publish(LocalDateTime createdDate, LocalDateTime modifiedDate,
		String createdBy, String updatedBy, String url, UUID projectId,
		String content,
		int viewCount) {
		super(createdDate, modifiedDate, createdBy, updatedBy);
		this.url = url;
		this.projectId = projectId;
		this.content = content;
		this.viewCount = viewCount;
	}

	public void updateContent(String content){
		this.content = content;
	}
}
