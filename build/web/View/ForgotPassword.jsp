<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quên mật khẩu - Quản lý phòng trọ</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
    </head>
    <body>
        <div class="login-wrapper">
            <div class="login-card">
                <div class="login-header">
                    <h1>Khôi phục tài khoản</h1>
                    <p>Xác minh qua câu hỏi bảo mật</p>
                </div>
                <div class="login-body">
                    <c:if test="${not empty errorMessage}"><div class="error">${errorMessage}</div></c:if>
                    
                    <form action="ForgotPassword" method="post">
                        <%-- Bước 1 --%>
                        <c:if test="${empty step || step == 1}">
                            <div class="field">
                                <label class="label">Tên tài khoản</label>
                                <input class="input" type="text" name="username" placeholder="Nhập username..." required />
                            </div>
                            <button class="btn" type="submit" name="action" value="checkUser">TIẾP THEO</button>
                        </c:if>

                        <%-- Bước 2 --%>
                        <c:if test="${step == 2}">
                            <div class="field">
                                <label class="label">Câu hỏi của bạn:</label>
                                <p style="color:#fff; font-style:italic; margin-bottom:15px;">"${question}"</p>
                                <label class="label">Câu trả lời</label>
                                <input class="input" type="text" name="answer" placeholder="Nhập câu trả lời..." required />
                                <input type="hidden" name="username" value="${targetUser}">
                            </div>
                            <button class="btn" type="submit" name="action" value="verifyAnswer">XÁC MINH</button>
                        </c:if>

                        <%-- Bước 3 --%>
                        <c:if test="${step == 3}">
                            <div class="field">
                                <label class="label">Mật khẩu mới</label>
                                <input class="input" type="password" name="newPass" placeholder="Nhập mật khẩu mới..." required />
                            </div>
                            
                            <%-- THÊM Ô XÁC NHẬN MẬT KHẨU VÀO ĐÂY --%>
                            <div class="field" style="margin-top: 15px;">
                                <label class="label">Xác nhận mật khẩu</label>
                                <input class="input" type="password" name="confirmPass" placeholder="Nhập lại mật khẩu mới..." required />
                            </div>
                            
                            <input type="hidden" name="username" value="${targetUser}">
                            
                            <button class="btn" type="submit" name="action" value="resetPass" style="margin-top: 20px;">CẬP NHẬT</button>
                        </c:if>
                        
                        <div class="row" style="justify-content: center; margin-top:20px;">
                            <a class="link" href="Login">Quay lại đăng nhập</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>