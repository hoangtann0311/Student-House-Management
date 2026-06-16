package Model;

public class User {

    private int userId;
    private String username;
    private String password;
    private int roleId;
    private int isActive;
    private boolean renting;
    private String securityQuestion;
    private String securityAnswer;
    private String fullName;

    public User() {
    }

    // Constructor dùng khi load từ DB (mặc định chưa check renting)
    public User(int userId, String username, String password,
            int roleId, int isActive) {

        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.isActive = isActive;
        this.renting = false; // mặc định
    }

    public User(int userId, String username, String password, int roleId, int isActive, boolean renting, String securityQuestion, String securityAnswer) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.isActive = isActive;
        this.renting = renting;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isRenting() {
        return renting;
    }

    public void setRenting(boolean renting) {
        this.renting = renting;
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
