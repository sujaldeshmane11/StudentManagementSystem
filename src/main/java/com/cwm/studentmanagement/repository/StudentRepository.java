
package com.cwm.studentmanagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cwm.studentmanagement.model.Students;

public interface StudentRepository extends JpaRepository<Students, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("""
        SELECT s FROM Students s
        WHERE s.active = true
        AND (
            :keyword IS NULL OR :keyword = ''
            OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(s.phonenumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """)
    Page<Students> searchActiveStudents(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Page<Students> findByActiveTrue(Pageable pageable);

    List<Students> findByActiveTrueOrderByFirstNameAsc();

    long countByActiveTrue();

    long countByActiveTrueAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Students> findTop5ByActiveTrueOrderByCreatedAtDesc();

    @Query(value = """
            SELECT c.course_code
            FROM student_courses sc
            JOIN students s ON sc.student_id = s.id
            JOIN courses c ON sc.course_id = c.id
            WHERE s.active = 1
              AND c.active = 1
            GROUP BY c.id, c.course_code
            ORDER BY COUNT(DISTINCT sc.student_id) DESC
            LIMIT 1
            """, nativeQuery = true)
    String findTopCourseCode();
}
