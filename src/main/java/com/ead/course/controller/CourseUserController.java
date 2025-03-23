package com.ead.course.controller;

import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(
            @PageableDefault(sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
            @PathVariable UUID courseId,
            SpecificationTemplate.UserSpec spec
    ) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(
            @RequestBody SubscriptionDto subscriptionDto,
            @PathVariable UUID courseId
    ) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        if (courseService.existsByCourseAndUser(courseId, subscriptionDto.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");
        }
        Optional<UserModel> userModelOptional = userService.findById(subscriptionDto.getUserId());
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if (userModelOptional.get().getUserStatus().equals(UserStatus.BLOCKED.toString())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked.");
        }

        courseService.saveSubscriptionUserInCourseAndSendNotification(courseModelOptional.get(), userModelOptional.get());

        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully");
    }
}
