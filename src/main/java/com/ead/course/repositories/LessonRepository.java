package com.ead.course.repositories;

import com.ead.course.models.LessonModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<LessonModel, UUID> {

    List<LessonModel> findAllByModule_ModuleId(UUID moduleId);

    Optional<LessonModel> findByModule_ModuleIdAndLessonId(UUID moduleId, UUID lessonId);
}
