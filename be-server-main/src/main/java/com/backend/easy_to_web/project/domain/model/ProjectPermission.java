package com.backend.easy_to_web.project.domain.model;

public enum ProjectPermission {
    READ_ONLY(1), // 읽기 전용
    EDIT(2),      // 수정 가능
    ADMIN(3),     // 관리자
    OWNER(4);     // 소유자

    private final int level;

    ProjectPermission(int level) {
        this.level = level;
    }

    public boolean isValidPermission(ProjectPermission requiredPermission) {
        return this.level >= requiredPermission.level;
    }
}
