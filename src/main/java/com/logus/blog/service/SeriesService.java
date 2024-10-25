package com.logus.blog.service;

import com.logus.blog.entity.Category;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Series;
import com.logus.blog.repository.CategoryRepository;
import com.logus.blog.repository.SeriesRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;

    public Series getReferenceById(Long seriesId) {
        return seriesId == null ? null :
                seriesRepository.getReferenceById(seriesId);
    }

    public Series getById(Long seriesId) {
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SERIES_NOT_FOUND));
    }


}
