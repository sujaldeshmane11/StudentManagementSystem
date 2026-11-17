
package com.cwm.studentmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cwm.studentmanagement.dto.StudentDTO;
import com.cwm.studentmanagement.repository.CourseRepository;
import com.cwm.studentmanagement.service.StudentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final CourseRepository courseRepository;

    public StudentController(StudentService studentService,
                             CourseRepository courseRepository) {
        this.studentService = studentService;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/new")
    public String showStudentForm(Model model) {
        model.addAttribute("studentDTO", new StudentDTO());
        model.addAttribute("courses", courseRepository.findByActiveTrue());
        return "add-student";
    }

    @GetMapping("/list")
    public String getStudents(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<StudentDTO> students =
                studentService.getStudents(page, size, keyword);

        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);

        return "students";
    }

    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        StudentDTO studentDTO = studentService.getStudentById(id);
        model.addAttribute("studentDTO", studentDTO);
        return "view-student";
    }

    @PostMapping("/save")
    public String saveStudent(
            @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("courses", courseRepository.findByActiveTrue());
            return "add-student";
        }

        if (studentService.existsByEmailIgnoreCase(studentDTO.getEmail())) {
            result.rejectValue("email", "error.studentDTO", "Email already exists");
            model.addAttribute("courses", courseRepository.findByActiveTrue());
            return "add-student";
        }

        studentService.createStudent(studentDTO);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Student added successfully!");

        return "redirect:/students/list";
    }

    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        StudentDTO studentDTO = studentService.getStudentById(id);
        model.addAttribute("studentDTO", studentDTO);
        model.addAttribute("courses", courseRepository.findByActiveTrue());
        return "edit-student";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(
            @PathVariable Long id,
            @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("courses", courseRepository.findByActiveTrue());
            return "edit-student";
        }

        if (studentService.existsByEmailIgnoreCaseAndIdNot(
                studentDTO.getEmail(), id)) {
            result.rejectValue("email", "error.studentDTO", "Email already exists");
            model.addAttribute("courses", courseRepository.findByActiveTrue());
            return "edit-student";
        }

        studentService.updateStudent(id, studentDTO);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Student updated successfully!");

        return "redirect:/students/list";
    }

    // Delete Student (Soft Delete)
    @PostMapping("/delete/{id}")
    public String deleteStudent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        studentService.deleteStudent(id);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Student deleted successfully!");

        return "redirect:/students/list";
    }
}
