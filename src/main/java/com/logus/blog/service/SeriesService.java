package com.logus.blog.service;

import com.logus.blog.entity.Category;
import com.logus.blog.entity.Series;
import com.logus.blog.repository.CategoryRepository;
import com.logus.blog.repository.SeriesRepository;
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

}
