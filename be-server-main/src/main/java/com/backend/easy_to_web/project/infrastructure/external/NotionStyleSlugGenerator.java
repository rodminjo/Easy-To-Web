package com.backend.easy_to_web.project.infrastructure.external;

import com.backend.easy_to_web.project.domain.port.out.SlugGenerator;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class NotionStyleSlugGenerator implements SlugGenerator {

    @Override
    public String generate(String title, String uuid) {
        String slug = slugify(title);
        return slug + "-" + uuid;
    }

    private String slugify(String input) {
        return Arrays.stream(input.split("\\s+"))
            .map(String::trim)
            .filter(s -> s.matches("^[a-zA-Z]+$"))
            .map(String::toLowerCase)
            .collect(Collectors.joining("-"));
    }
}
