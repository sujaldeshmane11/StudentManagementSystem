
package com.cwm.studentmanagement.service.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cwm.studentmanagement.dto.CourseDTO;
import com.cwm.studentmanagement.model.Courses;
import com.cwm.studentmanagement.repository.CourseRepository;
import com.cwm.studentmanagement.service.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger log =
            LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final ModelMapper mapper;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            ModelMapper mapper) {
        this.courseRepository = courseRepository;
        this.mapper = mapper;
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {

        log.info("Creating course with code: {}", courseDTO.getCourseCode());

        Courses course = mapper.map(courseDTO, Courses.class);
        course = courseRepository.save(course);

        return mapper.map(course, CourseDTO.class);
    }

    @Override
    public boolean existsByCourseCode(String code) {
        return courseRepository.existsByCourseCodeIgnoreCase(code);
    }

    @Override
    public boolean existsByCourseCodeAndIdNot(String code, Long id) {
        return courseRepository.existsByCourseCodeIgnoreCaseAndIdNot(code, id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> getCourses(int page, int size) {
        return getCourses(page, size, "");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> getCourses(
            int page, int size, String keyword) {

        log.info("Getting courses. Page: {}, Search: {}", page, keyword);

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "id")
        );

        String search = keyword == null ? "" : keyword.trim();

        return courseRepository.searchActiveCourses(search, pageable)
                .map(course -> mapper.map(course, CourseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {

        Courses course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No course found with id: " + id));

        return mapper.map(course, CourseDTO.class);
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {

        Courses course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No course found with id: " + id));

        mapper.map(courseDTO, course);

        Courses updatedCourse = courseRepository.save(course);

        return mapper.map(updatedCourse, CourseDTO.class);
    }

    @Override
    public void deleteCourse(Long id) {

        Courses course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No course found with id: " + id));

        course.setActive(false);

        courseRepository.save(course);

        log.info("Course deleted successfully. ID: {}", id);
    }
}
