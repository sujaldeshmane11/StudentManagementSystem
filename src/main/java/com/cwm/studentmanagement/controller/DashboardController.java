
package com.cwm.studentmanagement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cwm.studentmanagement.model.Students;
import com.cwm.studentmanagement.repository.StudentRepository;
import com.cwm.studentmanagement.repository.CourseRepository;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public DashboardController(
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Total Active Students
        long totalStudents = studentRepository.countByActiveTrue();

        // Total Active Courses
        long totalCourses = courseRepository.countByActiveTrue();

        // All Active Students
        List<Students> allStudents =
                studentRepository.findByActiveTrueOrderByFirstNameAsc();

        // Enrolled Students Count
        long enrolledStudents = allStudents.stream()
                .filter(student ->
                        student.getCourses() != null
                        && !student.getCourses().isEmpty())
                .count();

        // Current Month Student Registrations
        LocalDate today = LocalDate.now();

        LocalDateTime startOfMonth = today
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime startOfNextMonth = today
                .withDayOfMonth(1)
                .plusMonths(1)
                .atStartOfDay();

        long monthlyStudents =
                studentRepository
                .countByActiveTrueAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        startOfMonth,
                        startOfNextMonth
                );

        // Top Performing Courses
        Map<String, Long> topCourses = allStudents.stream()
                .filter(student -> student.getCourses() != null)
                .flatMap(student -> student.getCourses().stream())
                .collect(Collectors.groupingBy(
                        course -> course.getCourseName(),
                        Collectors.counting()
                ));

        // Sort Courses by Enrollment Count
        Map<String, Long> sortedTopCourses = topCourses.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));

        // Recent 5 Students
        var recentStudents =
                studentRepository.findTop5ByActiveTrueOrderByCreatedAtDesc();

        // Send Data to Dashboard
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("enrolledStudents", enrolledStudents);
        model.addAttribute("monthlyStudents", monthlyStudents);
        model.addAttribute("topCourses", sortedTopCourses);
        model.addAttribute("recentStudents", recentStudents);

        return "dashboard";
    }
}
