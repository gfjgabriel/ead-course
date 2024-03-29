package com.ead.course.services;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UtilsService {

    String createUrlGetAllUsersByCourse(UUID courseId, Pageable pageable);

    String createUrlGetOneUserById(UUID userId);

    String createUrlPostSubscription(UUID userId);

    String createUrlDeleteCourse(UUID courseId);
}
