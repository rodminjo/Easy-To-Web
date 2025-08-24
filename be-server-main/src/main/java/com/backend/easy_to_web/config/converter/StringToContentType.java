package com.backend.easy_to_web.config.converter;

import com.backend.easy_to_web.file.domain.model.ContentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * EncodeType enum 소문자도 매핑되도록 수정
 */

@Component
public class StringToContentType implements Converter<String, ContentType> {

    @Override
    public ContentType convert(String source) {
        return ContentType.fromExtension(source);
    }
}