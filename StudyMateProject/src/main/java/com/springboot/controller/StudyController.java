package com.springboot.controller;

import com.springboot.config.SessionConst;
import com.springboot.dto.StudyForm;
import com.springboot.entity.Study;
import com.springboot.service.StudyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.springboot.dto.StudySearchCondition;

@Controller
@RequestMapping("/studies")
public class StudyController {

    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    // 스터디 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         @SessionAttribute(name = SessionConst.LOGIN_USER_ID, required = false) Long loginUserId,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            Study study = studyService.findById(id);
            model.addAttribute("study", study);
            model.addAttribute("isLeader", study.isLeader(loginUserId));
            return "studies/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/studies";
        }
    }

    // 스터디 생성 화면 (로그인 필요 - LoginCheckInterceptor)
    @GetMapping("/new")
    public String createForm(Model model) {
        StudyForm form = new StudyForm();
        form.setMaxMembers(4);
        model.addAttribute("form", form);
        model.addAttribute("mode", "create");
        return "studies/form";
    }

    // 스터디 생성 요청 처리
    @PostMapping("/new")
    public String create(@ModelAttribute("form") StudyForm form,
                         BindingResult bindingResult,
                         @SessionAttribute(name = SessionConst.LOGIN_USER_ID) Long loginUserId,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "모집 인원은 숫자로 입력해주세요.");
            model.addAttribute("mode", "create");
            return "studies/form";
        }

        try {
            Study study = studyService.create(loginUserId, form);
            redirectAttributes.addFlashAttribute("message", "스터디가 생성되었습니다.");
            return "redirect:/studies/" + study.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mode", "create");
            return "studies/form";
        }
    }

    // 스터디 수정 화면
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id,
                           @SessionAttribute(name = SessionConst.LOGIN_USER_ID) Long loginUserId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            Study study = studyService.findOwnedStudy(id, loginUserId);
            model.addAttribute("form", StudyForm.from(study));
            model.addAttribute("studyId", id);
            model.addAttribute("mode", "edit");
            return "studies/form";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/studies";
        }
    }

    // 스터디 수정 요청 처리
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id,
                       @ModelAttribute("form") StudyForm form,
                       BindingResult bindingResult,
                       @SessionAttribute(name = SessionConst.LOGIN_USER_ID) Long loginUserId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "모집 인원은 숫자로 입력해주세요.");
            model.addAttribute("studyId", id);
            model.addAttribute("mode", "edit");
            return "studies/form";
        }

        try {
            studyService.update(id, loginUserId, form);
            redirectAttributes.addFlashAttribute("message", "스터디가 수정되었습니다.");
            return "redirect:/studies/" + id;
        } catch (IllegalArgumentException e) {
            // 입력값 오류 → 수정 화면 유지
            model.addAttribute("error", e.getMessage());
            model.addAttribute("studyId", id);
            model.addAttribute("mode", "edit");
            return "studies/form";
        } catch (IllegalStateException e) {
            // 권한 없음
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/studies";
        }
    }

    // 스터디 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         @SessionAttribute(name = SessionConst.LOGIN_USER_ID) Long loginUserId,
                         RedirectAttributes redirectAttributes) {
        try {
            studyService.delete(id, loginUserId);
            redirectAttributes.addFlashAttribute("message", "스터디가 삭제되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/studies";
    }

    @GetMapping
    public String list(@ModelAttribute("cond") StudySearchCondition cond, Model model) {
        model.addAttribute("studies", studyService.search(cond));
        return "studies/list";
    }
}
