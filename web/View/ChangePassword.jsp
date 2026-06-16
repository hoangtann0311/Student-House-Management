<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Đổi mật khẩu</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <main class="container main">
            <div class="center-wrap">
                <div class="center-card" style="width: 100%; max-width: 450px;">
                    <div class="form-header-area" style="justify-content: center;">
                        <h2 class="page-title text-center">Đổi Mật Khẩu</h2>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert-danger" style="margin-bottom: 15px;">${error}</div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="success-alert" style="margin-bottom: 15px;">${success}</div>
                    </c:if>

                    <form action="ChangePassword" method="POST">
                        
                        <div class="form-group">
                            <label class="required">Mật khẩu hiện tại</label>
                            <input type="password" class="input" name="oldPassword" placeholder="Nhập mật khẩu hiện tại..." required />
                            
                            <div style="text-align: right; margin-top: 6px; margin-right: 265px">
                                <a href="${pageContext.request.contextPath}/ForgotPassword" style="font-size: 13px; color: #1b66d1; text-decoration: none; font-weight: 600;">
                                    Quên mật khẩu hiện tại?
                                </a>
                            </div>
                        </div>
                        
                        <div class="form-group" style="margin-top: 10px;">
                            <label class="required">Mật khẩu mới</label>
                            <input type="password" class="input" name="newPassword" placeholder="Nhập mật khẩu mới..." required />
                        </div>
                        
                        <div class="form-group">
                            <label class="required">Xác nhận mật khẩu mới</label>
                            <input type="password" class="input" name="confirmPassword" placeholder="Nhập lại mật khẩu mới..." required />
                        </div>

                        <div class="form-actions" style="margin-top: 25px;">
                            <button class="btn btn-main" type="submit" style="width: 100%; padding: 12px; font-size: 16px;">XÁC NHẬN ĐỔI</button>
                        </div>
                        
                    </form>
                </div>
            </div>
        </main>
    </body>
</html>