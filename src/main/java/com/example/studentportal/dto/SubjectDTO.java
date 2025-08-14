package com.example.studentportal.dto;

public class SubjectDTO {

    private Long id;
    private String code;
    private String name;
    private String semester;

    public SubjectDTO(Long id, String code, String name, String semester) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.semester = semester;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
