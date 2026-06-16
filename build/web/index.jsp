<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Đăng nhập - Quản lý phòng trọ</title>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
    </head>
    <body>
        <div class="login-wrapper">
            <div class="login-card">
                <div class="login-header">
                    <h1>Quản lý phòng trọ</h1>
                    <p>Đăng nhập để tiếp tục</p>
                </div>

                <div class="login-body">
                    <%-- Hiển thị thông báo lỗi (Màu hồng nhạt như trong ảnh) --%>
                    <c:if test="${not empty errorMessage}">
                        <div class="error">${errorMessage}</div>
                    </c:if>

                    <form action="Login" method="post">
                        <div class="field">
                            <label class="label">Tài khoản</label>
                            <div class="input-wrap">
                                <input class="input" type="text" name="username" value="${param.username}" placeholder="Nhập tên đăng nhập" required />
                            </div>
                        </div>

                        <div class="field" style="margin-top: 15px;">
                            <label class="label">Mật khẩu</label>
                            <div class="input-wrap">
                                <input class="input" type="password" name="password" placeholder="Nhập mật khẩu" required />
                            </div>
                        </div>

                        <div class="row" style="margin: 15px 0;">
                            <a class="link" href="ForgotPassword">Quên mật khẩu?</a>
                        </div>

                        <button class="btn" type="submit">Đăng nhập</button>
                    </form>
                </div>
            </div>          
        </div>
    </body>
</html>