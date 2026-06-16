<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Hồ sơ của tôi</title>
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
                        <h2 class="page-title">Thông tin cá nhân</h2>
                        <span class="badge ${profile.roleId == 1 ? 'maintenance' : 'available'}">
                            Vai trò: ${profile.roleId == 1 ? 'ADMIN' : 'STUDENT'}
                        </span>
                    </div>

                    <c:if test="${not empty success}">
                        <div class="success-alert">${success}</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert-danger">${error}</div>
                    </c:if>

                    <form action="Profile" method="POST">
                        <div class="form-row">
                            <div class="form-group">
                                <label>Tên đăng nhập (Tài khoản)</label>
                                <input class="input" value="${profile.username}" readonly style="background: #f1f5f9; cursor: not-allowed;" />
                                <small>Tên đăng nhập không thể thay đổi.</small>
                            </div>
                        </div>

                        <%-- NẾU LÀ SINH VIÊN (ROLE = 2) THÌ HIỆN BẢNG THÔNG TIN NÀY --%>
                        <c:if test="${profile.roleId == 2}">
                            <div style="border-top: 1px solid #cbd5e1; margin-top: 15px; padding-top: 20px;">
                                <h3 style="margin-bottom: 15px; color: #0284c7;">Hồ sơ sinh viên</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label class="required">Họ và tên</label>
                                        <input class="input" type="text" name="FullName" value="${profile.fullName}" required />
                                    </div>
                                    <div class="form-group">
                                        <label class="required">Số CCCD</label>
                                        <input class="input" type="text" name="CCCD" value="${profile.studentCode}" required />
                                    </div>
                                    <div class="form-group">
                                        <label>Ngày sinh</label>
                                        <input class="input" type="date" name="DOB" value="${profile.dob}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Giới tính</label>
                                        <select class="input" name="Gender">
                                            <option value="Nam" ${profile.gender == 'Nam' ? 'selected' : ''}>Nam</option>
                                            <option value="Nữ" ${profile.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                        </select>
                                    </div>
                                    <div class="form-group" style="grid-column: span 2;">
                                        <label>Số điện thoại liên hệ</label>
                                        <input class="input" type="text" name="Phone" value="${profile.phone}" />
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <%-- PHẦN CÂU HỎI BẢO MẬT (AI CŨNG CÓ) --%>
                        <div style="border-top: 1px dashed #cbd5e1; margin-top: 20px; padding-top: 20px;">
                            <h3 style="margin-bottom: 15px; color: #3d463d;">Bảo mật khôi phục mật khẩu</h3>
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="required">Câu hỏi bảo mật</label>
                                    <select class="input" name="SecurityQuestion" required>
                                        <option value="Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?" ${profile.securityQuestion == 'Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?' ? 'selected' : ''}>
                                            Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?
                                        </option>
                                        <option value="Tên con vật đầu tiên của bạn?" ${profile.securityQuestion == 'Tên con vật đầu tiên của bạn?' ? 'selected' : ''}>
                                            Tên con vật đầu tiên của bạn?
                                        </option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="required">Câu trả lời bí mật</label>
                                    <input class="input" name="SecurityAnswer" value="${profile.securityAnswer}" required />
                                </div>
                            </div>
                        </div>

                        <div class="form-actions">
                            <button class="btn btn-main" type="submit" style="width: 100%;">LƯU THAY ĐỔI</button>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </body>
</html>