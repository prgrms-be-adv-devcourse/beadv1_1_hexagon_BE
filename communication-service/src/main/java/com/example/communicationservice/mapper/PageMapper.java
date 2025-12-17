package com.example.communicationservice.mapper;

import com.example.communicationservice.controller.dto.response.PageInfo;
import org.springframework.data.domain.Page;

// dto <-> entity 변환 로직 전담
public abstract class PageMapper {

    private PageMapper() {} // 인스턴스화 방지

    public static PageInfo toPageInfo(Page<?> page) {
        return new PageInfo(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext()
        );
    }

}
