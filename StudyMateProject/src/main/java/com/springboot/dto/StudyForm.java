package com.springboot.dto;

import com.springboot.entity.Study;

/**
 * 스터디 생성/수정 화면의 입력값을 담는 객체
 */
public class StudyForm {

    private String title;
    private String description;
    private Integer maxMembers;

    public StudyForm() {
    }

    public static StudyForm from(Study study) {
        StudyForm form = new StudyForm();
        form.setTitle(study.getTitle());
        form.setDescription(study.getDescription());
        form.setMaxMembers(study.getMaxMembers());
        return form;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }
}
