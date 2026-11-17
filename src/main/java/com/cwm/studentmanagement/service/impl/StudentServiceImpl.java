
package com.cwm.studentmanagement.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cwm.studentmanagement.dto.StudentDTO;
import com.cwm.studentmanagement.model.Courses;
import com.cwm.studentmanagement.model.Students;
import com.cwm.studentmanagement.repository.CourseRepository;
import com.cwm.studentmanagement.repository.StudentRepository;
import com.cwm.studentmanagement.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentServiceImpl(
            StudentRepository studentRepository,
            CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return studentRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id) {
        return studentRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    @Override
    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {

        Students student = new Students();

        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setPhonenumber(studentDTO.getPhoneNumber());
        student.setAddress(studentDTO.getAddress());

        student.setCourses(getCoursesByIds(studentDTO.getCourseIds()));

        return convertToDTO(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> getStudents(int page, int size) {
        return getStudents(page, size, "");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> getStudents(int page, int size, String keyword) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1)
        );

        String search = keyword == null ? "" : keyword.trim();

        return studentRepository.searchActiveStudents(search, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {

        Students student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with ID: " + id));

        return convertToDTO(student);
    }

    @Override
    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {

        Students student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with ID: " + id));

        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setPhonenumber(studentDTO.getPhoneNumber());
        student.setAddress(studentDTO.getAddress());

        student.setCourses(getCoursesByIds(studentDTO.getCourseIds()));

        return convertToDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public StudentDTO enrollCourses(Long studentId, List<Long> courseIds) {

        Students student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with ID: " + studentId));

        if (courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one course.");
        }

        Set<Courses> newCourses = getCoursesByIds(courseIds);
        student.getCourses().addAll(newCourses);

        return convertToDTO(studentRepository.save(student));
    }

    // Soft Delete Student
    @Override
    @Transactional
    public void deleteStudent(Long id) {

        Students student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with ID: " + id));

        student.setActive(false);

        studentRepository.save(student);
    }

    private Set<Courses> getCoursesByIds(List<Long> courseIds) {

        if (courseIds == null || courseIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(courseIds);
        List<Courses> courses = courseRepository.findAllById(uniqueIds);

        if (courses.size() != uniqueIds.size()) {
            throw new IllegalArgumentException(
                    "One or more selected courses are invalid.");
        }

        return new LinkedHashSet<>(courses);
    }

    private StudentDTO convertToDTO(Students student) {

        StudentDTO dto = new StudentDTO();

        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setPhoneNumber(student.getPhonenumber());
        dto.setAddress(student.getAddress());
        dto.setActive(student.isActive());

        List<Long> courseIds = student.getCourses()
                .stream()
                .map(Courses::getId)
                .collect(Collectors.toList());

        List<String> courseNames = student.getCourses()
                .stream()
                .map(Courses::getCourseName)
                .collect(Collectors.toList());

        List<String> courseDetails = student.getCourses()
                .stream()
                .map(course ->
                        course.getCourseName()
                        + " (" + course.getCourseCode() + ")"
                        + " - ₹" + course.getFee())
                .collect(Collectors.toList());

        dto.setCourseIds(courseIds);
        dto.setCourseNames(courseNames);
        dto.setCourseDetails(courseDetails);

        return dto;
    }
}
