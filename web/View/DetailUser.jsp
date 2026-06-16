<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Chi tiết tài khoản</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <main class="container main">
            <div class="center-wrap">
                <div class="center-card" style="width: 100%; max-width: 600px;">
                    <div class="form-header-area">
                        <h2 class="page-title">Thông tin tài khoản chi tiết</h2>
                        <a class="btn-back" href="${pageContext.request.contextPath}/User">← Danh sách</a>
                    </div>

                    <div class="form-row">
                        <%-- THÔNG TIN CƠ BẢN --%>
                        <div class="form-group">
                            <label>Username</label>
                            <input class="input font-bold" value="${student.username}" readonly />
                        </div>
                        <div class="form-group">
                            <label>Mật khẩu hiện tại</label>
                            <input class="input" value="${student.password}" readonly />
                        </div>
                        <div class="form-group">
                            <label>Vai trò</label>
                            <span class="badge ${student.roleId == 1 ? 'maintenance' : 'available'}" style="width: fit-content; padding: 10px 20px;">
                                ${student.roleId == 1 ? 'Quản trị viên (Admin)' : 'Sinh viên (Student)'}
                            </span>
                        </div>
                        <div class="form-group">
                            <label>Trạng thái</label>
                            <input class="input ${student.isActive == 1 ? 'text-success' : 'text-danger'} font-bold" 
                                   value="${student.isActive == 1 ? 'Đang hoạt động' : 'Đang bị khóa'}" readonly />
                        </div>
                    </div>

                    <%-- PHẦN THÔNG TIN BẢO MẬT --%>
                    <div style="border-top: 1px dashed #cbd5e1; margin-top: 15px; padding-top: 15px; background: rgba(0,0,0,0.02); padding: 15px; border-radius: 8px;">
                        <h3 style="margin-top: 0; color: #3d463d; font-size: 16px;">Thông tin khôi phục mật khẩu</h3>
                        <div class="form-group">
                            <label>Câu hỏi bảo mật</label>
                            <input class="input" value="${student.securityQuestion}" readonly style="font-style: italic;" />
                        </div>
                        <div class="form-group">
                            <label>Câu trả lời bí mật</label>
                            <input class="input text-primary font-bold" value="${student.securityAnswer}" readonly />
                        </div>
                    </div>

                    <%-- HỒ SƠ SINH VIÊN (NẾU CÓ) --%>
                    <c:if test="${student.roleId == 2}">
                        <div style="border-top: 1px solid #cbd5e1; margin-top: 15px; padding-top: 15px;">
                            <h3 style="margin-top: 0; color: #0284c7; font-size: 16px;">Hồ sơ Sinh viên</h3>
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Họ và tên</label>
                                    <input class="input" value="${student.fullName}" readonly />
                                </div>
                                <div class="form-group">
                                    <label>Số CCCD</label>
                                    <input class="input" value="${student.studentCode}" readonly />
                                </div>
                                <div class="form-group">
                                    <label>Ngày sinh</label>
                                    <input class="input" value="${student.dob}" readonly />
                                </div>
                                <div class="form-group">
                                    <label>Giới tính</label>
                                    <input class="input" value="${student.gender == 'Male' || student.gender == 'Nam' ? 'Nam' : (student.gender == 'Female' || student.gender == 'Nữ' ? 'Nữ' : 'Khác')}" readonly />
                                </div>
                                <div class="form-group" style="grid-column: span 2;">
                                    <label>Số điện thoại</label>
                                    <input class="input" value="${student.phone}" readonly />
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <div class="form-actions" style="margin-top: 20px;">
                        <a class="btn btn-main" href="${pageContext.request.contextPath}/EditUser?id=${student.userId}">Chỉnh sửa thông tin</a>
                    </div>
                </div>
            </div>
        </main>
    </body>
</html>