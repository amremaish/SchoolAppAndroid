package com.example.SchoolApp.Models;

public class StudentExamAdapter {

    private String studentEmail ,exam_id, grade , max_grade , exam_name , student_name ;

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getExam_id() {
        return exam_id;
    }

    public void setExam_id(String exam_id) {
        this.exam_id = exam_id;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMax_grade() {
        return max_grade;
    }

    public void setMax_grade(String max_grade) {
        this.max_grade = max_grade;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }
}
