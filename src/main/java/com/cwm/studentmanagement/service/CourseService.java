
package com.cwm.studentmanagement.service;

import org.springframework.data.domain.Page;

import com.cwm.studentmanagement.dto.CourseDTO;

public interface CourseService {

    CourseDTO createCourse(CourseDTO courseDTO);

    boolean existsByCourseCode(String code);

    boolean existsByCourseCodeAndIdNot(String code, Long id);

    Page<CourseDTO> getCourses(int page, int size);

    Page<CourseDTO> getCourses(int page, int size, String keyword);

    CourseDTO getCourseById(Long id);

    CourseDTO updateCourse(Long id, CourseDTO courseDTO);

    // Soft Delete Course
    void deleteCourse(Long id);
}
