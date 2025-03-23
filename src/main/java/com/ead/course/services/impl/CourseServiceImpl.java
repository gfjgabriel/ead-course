package com.ead.course.services.impl;

import com.ead.course.dtos.NotificationCommandDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.UserModel;
import com.ead.course.publishers.NotificationCommandPublisher;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.CourseService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationCommandPublisher notificationCommandPublisher;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {
        List<ModuleModel> modules = moduleRepository.findAllByCourse_courseId(courseModel.getCourseId());
        if (!modules.isEmpty()) {
            for (ModuleModel module : modules) {
                List<LessonModel> lessons = lessonRepository.findAllByModule_ModuleId(module.getModuleId());
                if (!lessons.isEmpty()) {
                    lessonRepository.deleteAll(lessons);
                }
            }
            moduleRepository.deleteAll(modules);
        }
        courseRepository.deleteCourseUserByCourse(courseModel.getCourseId());
        courseRepository.delete(courseModel);
    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return courseRepository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return courseRepository.findAll(spec, pageable);
    }

    @Override
    public boolean existsByCourseAndUser(UUID courseId, UUID userId) {
        return courseRepository.existsByCourseAndUser(courseId, userId);
    }

    @Transactional
    @Override
    public void saveSubscriptionUserInCourse(UUID courseId, UUID userId) {
        courseRepository.saveSubscriptionUserInCourse(courseId, userId);
    }

    @Transactional
    @Override
    public void saveSubscriptionUserInCourseAndSendNotification(CourseModel course, UserModel user) {
        courseRepository.saveSubscriptionUserInCourse(course.getCourseId(), user.getUserId());
        try {
            NotificationCommandDTO notificationCommandDTO = new NotificationCommandDTO();
            notificationCommandDTO.setTitle("Bem-Vindo(a) ao Curso: " + course.getName());
            notificationCommandDTO.setMessage(user.getFullName() + ", você foi inscrito no curso " + course.getName());
            notificationCommandDTO.setUserId(user.getUserId());
            notificationCommandPublisher.publishNotificationCommand(notificationCommandDTO);
        } catch (Exception e) {
            log.warn("Error sending notification to user {}", user.getUserId(), e);
        }
    }
}
