
package com.cwm.studentmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cwm.studentmanagement.model.Courses;

public interface CourseRepository extends JpaRepository<Courses, Long> {

    boolean existsByCourseCodeIgnoreCase(String code);

    boolean existsByCourseCodeIgnoreCaseAndIdNot(String code, Long id);

    @Query("""
        SELECT c FROM Courses c
        WHERE c.active = true
        AND (
            :keyword IS NULL OR :keyword = ''
            OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(c.duration, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """)
    Page<Courses> searchActiveCourses(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Page<Courses> findByActiveTrue(Pageable pageable);

    List<Courses> findByActiveTrue();

    long countByActiveTrue();
}
