package Model;

import java.sql.Date;

public class StudentUser {

    private int userId;
    private String username;
    private String password;
    private int roleId;
    private int isActive;

    private int studentId;
    private String fullName;
    private Date dob;
    private String gender;
    private String phone;
    private String studentCode;

    // 2 Thuộc tính bảo mật bắt buộc phải có
    private String securityQuestion;
    private String securityAnswer;

    public StudentUser() {
    }

    public StudentUser(int userId, String username, String password, int roleId, int isActive, int studentId, String fullName, Date dob, String gender, String phone, String studentCode, String securityQuestion, String securityAnswer) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.isActive = isActive;
        this.studentId = studentId;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.studentCode = studentCode;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
