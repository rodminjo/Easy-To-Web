package com.backend.easy_to_web.project.domain.port.out;

public interface SlugGenerator {
    String generate(String title, String uuid);
}
