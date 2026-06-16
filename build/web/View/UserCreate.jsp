<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Tạo tài khoản mới</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <main class="container main">
            <div class="center-wrap">
                <div class="center-card" style="width: 100%; max-width: 650px;">
                    <div class="form-header-area">
                        <h2 class="page-title">Tạo tài khoản mới</h2>
                        <a class="btn-back" href="${pageContext.request.contextPath}/User">← Danh sách</a>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert-box alert-danger">${errorMessage}</div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/UserCreate" method="post">
                        <%-- PHẦN 1: THÔNG TIN ĐĂNG NHẬP --%>
                        <div class="form-row">
                            <div class="form-group">
                                <label class="required">Username</label>
                                <input class="input" name="username" value="${username}" placeholder="Nhập tên đăng nhập..." required />
                            </div>

                            <div class="form-group">
                                <label class="required">Password</label>
                                <input class="input" type="password" name="password" placeholder="••••••••" required />
                            </div>

                            <div class="form-group">
                                <label class="required">Nhập lại password</label>
                                <input class="input" type="password" name="confirmPassword" placeholder="••••••••" required />
                            </div>

                            <div class="form-group">
                                <label class="required">Quyền truy cập (Role)</label>
                                <select class="input" name="roleId" required>
                                    <option value="2" ${roleId == '2' ? 'selected' : ''}>STUDENT (Sinh viên)</option>
                                    <option value="1" ${roleId == '1' ? 'selected' : ''}>ADMIN (Quản trị)</option>
                                </select>
                            </div>
                        </div>

                        <%-- PHẦN 2: THIẾT LẬP BẢO MẬT --%>
                        <div style="border-top: 1px dashed #cbd5e1; margin-top: 25px; padding-top: 20px;">
                            <h3 style="margin-bottom: 15px; color: #3d463d; font-size: 16px;">Thiết lập bảo mật khôi phục mật khẩu</h3>
                            
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="required">Câu hỏi bảo mật</label>
                                    <select class="input" name="securityQuestion" required>
                                        <option value="Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?">Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="required">Câu trả lời bí mật</label>
                                    <input class="input" name="securityAnswer" value="${securityAnswer}" placeholder="Nhập câu trả lời..." required />
                                </div>
                            </div>
                        </div>

                        <%-- PHẦN 3: THÔNG TIN CHI TIẾT SINH VIÊN --%>
                        <div style="border-top: 1px solid #cbd5e1; margin-top: 25px; padding-top: 20px; background: rgba(2, 132, 199, 0.03); border-radius: 8px; padding: 15px;">
                            <h3 style="margin-bottom: 15px; color: #0284c7; font-size: 16px;">Thông tin chi tiết cá nhân (Dành cho Student)</h3>

                            <div class="form-row">
                                <div class="form-group">
                                    <label>Họ và tên</label>
                                    <input class="input" name="fullName" value="${fullName}" placeholder="Bắt buộc nếu là Student" />
                                </div>
                                <div class="form-group">
                                    <label>Mã CCCD / Sinh viên</label>
                                    <input class="input" name="studentCode" value="${studentCode}" placeholder="Bắt buộc nếu là Student" />
                                </div>
                                <div class="form-group">
                                    <label>Ngày sinh</label>
                                    <input class="input" type="date" name="dob" value="${dob}" />
                                </div>
                                <div class="form-group">
                                    <label>Giới tính</label>
                                    <select class="input" name="gender">
                                        <option value="Nam" ${gender == 'Nam' ? 'selected' : ''}>Nam</option>
                                        <option value="Nữ" ${gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                    </select>
                                </div>
                                <div class="form-group" style="grid-column: span 2;">
                                    <label>Số điện thoại</label>
                                    <input class="input" name="phone" value="${phone}" placeholder="Nhập số điện thoại liên lạc..." />
                                </div>
                            </div>
                        </div>

                        <div class="form-actions" style="margin-top: 30px;">
                            <a class="btn btn-cancel" href="${pageContext.request.contextPath}/User">Huỷ bỏ</a>
                            <button class="btn btn-main" type="submit" style="min-width: 150px;">Tạo tài khoản</button>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </body>
</html>