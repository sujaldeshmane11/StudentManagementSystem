
package com.cwm.studentmanagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cwm.studentmanagement.dto.CourseDTO;
import com.cwm.studentmanagement.service.CourseService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/course")
public class CourseController {

    private static final Logger log =
            LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/new")
    public String showCreateCourse(Model model) {
        model.addAttribute("courseDTO", new CourseDTO());
        return "add-course";
    }

    @GetMapping("/list")
    public String listCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model,
            @RequestParam(value = "message", required = false) String message) {

        log.info("GET /course/list - showing course list.");

        keyword = keyword.trim();

        Page<CourseDTO> courses =
                courseService.getCourses(page, size, keyword);

        model.addAttribute("courses", courses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("message", message);

        return "courses";
    }

    @PostMapping
    public String createCourses(
            @Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "add-course";
        }

        if (courseService.existsByCourseCode(courseDTO.getCourseCode())) {
            bindingResult.rejectValue(
                    "courseCode", null, "Code must be unique");
            return "add-course";
        }

        courseService.createCourse(courseDTO);

        redirectAttributes.addFlashAttribute(
                "message", "Course created successfully!");

        return "redirect:/course/list";
    }

    @GetMapping("/{id}")
    public String getCourseById(
            @PathVariable Long id, Model model) {

        CourseDTO course = courseService.getCourseById(id);
        model.addAttribute("course", course);

        return "view-course";
    }

    @GetMapping("/{id}/edit")
    public String editCourse(
            @PathVariable Long id, Model model) {

        CourseDTO course = courseService.getCourseById(id);
        model.addAttribute("courseDTO", course);

        return "edit-course";
    }

    @PostMapping("/{id}/update")
    public String updateCourse(
            @PathVariable Long id,
            @Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "edit-course";
        }

        if (courseService.existsByCourseCodeAndIdNot(
                courseDTO.getCourseCode(), id)) {

            bindingResult.rejectValue(
                    "courseCode", null, "Code must be unique");

            return "edit-course";
        }

        courseService.updateCourse(id, courseDTO);

        redirectAttributes.addFlashAttribute(
                "message", "Course is updated successfully!");

        return "redirect:/course/list";
    }

    // Delete Course (Soft Delete)
    @PostMapping("/{id}/delete")
    public String deleteCourse(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        courseService.deleteCourse(id);

        redirectAttributes.addFlashAttribute(
                "message", "Course deleted successfully!");

        log.info("POST /course/{}/delete", id);

        return "redirect:/course/list";
    }
}
