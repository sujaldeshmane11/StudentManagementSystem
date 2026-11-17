
package com.cwm.studentmanagement.controller;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cwm.studentmanagement.model.Students;
import com.cwm.studentmanagement.repository.CourseRepository;
import com.cwm.studentmanagement.repository.StudentRepository;
import com.cwm.studentmanagement.service.StudentService;

@Controller
@RequestMapping("/enrollment")
public class EnrollmentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(
            StudentService studentService,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.studentService = studentService;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // Show Enrollment Page
    @GetMapping("/new")
    public String showEnrollmentPage(Model model) {

        loadEnrollmentData(model);

        return "enroll-course";
    }

    // Save Student Enrollment
    @PostMapping("/save")
    public String saveEnrollment(
            @RequestParam("studentId") Long studentId,
            @RequestParam(value = "courseIds", required = false) List<Long> courseIds,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (courseIds == null || courseIds.isEmpty()) {

            model.addAttribute(
                    "errorMessage",
                    "Please select at least one new course."
            );

            loadEnrollmentData(model);

            return "enroll-course";
        }

        try {

            studentService.enrollCourses(studentId, courseIds);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "New courses enrolled successfully! Existing courses are preserved."
            );

            return "redirect:/enrollment/students";

        } catch (RuntimeException exception) {

            model.addAttribute(
                    "errorMessage",
                    "Enrollment failed: " + exception.getMessage()
            );

            loadEnrollmentData(model);

            return "enroll-course";
        }
    }

    // Show All Enrolled Students
    @GetMapping("/students")
    public String showEnrolledStudents(Model model) {

        List<Students> enrolledStudents =
                studentRepository.findByActiveTrueOrderByFirstNameAsc()
                        .stream()
                        .filter(student ->
                                student.getCourses() != null
                                && !student.getCourses().isEmpty())
                        .collect(Collectors.toList());

        model.addAttribute("students", enrolledStudents);

        return "enrolled-students";
    }

    // Show Individual Student Enrollment Details
    @GetMapping("/details/{id}")
    public String showEnrollmentDetails(
            @PathVariable("id") Long studentId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Students student = studentRepository.findById(studentId)
                .orElse(null);

        if (student == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student not found."
            );

            return "redirect:/enrollment/students";
        }

        model.addAttribute("student", student);

        return "enrollment-details";
    }

    // Load Students, Courses and Existing Enrollments
    private void loadEnrollmentData(Model model) {

        List<Students> students =
                studentRepository.findByActiveTrueOrderByFirstNameAsc();

        model.addAttribute("students", students);

        model.addAttribute(
                "courses",
                courseRepository.findByActiveTrue()
        );

        // Store Already Enrolled Course IDs
        Map<Long, String> studentCourseIds = new LinkedHashMap<>();

        for (Students student : students) {

            String enrolledIds = student.getCourses()
                    .stream()
                    .map(course -> String.valueOf(course.getId()))
                    .collect(Collectors.joining(","));

            studentCourseIds.put(student.getId(), enrolledIds);
        }

        model.addAttribute("studentCourseIds", studentCourseIds);
    }
}
