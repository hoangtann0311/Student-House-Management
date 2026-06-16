<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Chỉnh sửa tài khoản</title>
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
                        <h2 class="page-title">Chỉnh sửa tài khoản</h2>
                        <a class="btn-back" href="${pageContext.request.contextPath}/User">← Danh sách</a>
                    </div>

                    <form action="EditUser" method="POST">
                        <%-- ID ẩn để gửi về Server --%>
                        <input type="hidden" name="UserID" value="${editUser.userId}" />

                        <div class="form-row">
                            <div class="form-group">
                                <label>Username</label>
                                <input class="input" name="Username" value="${editUser.username}" readonly style="background: #f1f5f9;" />
                            </div>
                            <div class="form-group">
                                <label class="required">Password</label>
                                <input class="input" type="text" name="Password" value="${editUser.password}" required />
                            </div>
                            <div class="form-group">
                                <label class="required">Quyền (Role)</label>
                                <select class="input" name="RoleID">
                                    <option value="1" ${editUser.roleId == 1 ? 'selected' : ''}>Admin</option>
                                    <option value="2" ${editUser.roleId == 2 ? 'selected' : ''}>Student</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="required">Trạng thái</label>
                                <select class="input" name="IsActive">
                                    <option value="1" ${editUser.isActive == 1 ? 'selected' : ''}>Đang hoạt động</option>
                                    <option value="0" ${editUser.isActive == 0 ? 'selected' : ''}>Bị khóa</option>
                                </select>
                            </div>
                        </div>

                        <%-- PHẦN THÊM MỚI: CÂU HỎI BẢO MẬT --%>
                        <div style="border-top: 1px dashed #cbd5e1; margin-top: 20px; padding-top: 20px;">
                            <h3 style="margin-bottom: 15px; color: #3d463d; font-size: 16px;">Thiết lập bảo mật khôi phục mật khẩu</h3>
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="required">Câu hỏi bảo mật</label>
                                    <select class="input" name="SecurityQuestion" required>
                                        <option value="Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?" 
                                            ${editUser.securityQuestion == 'Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?' ? 'selected' : ''}>
                                            Tên con vật/ đồ vật/ sở thích mà bạn yêu thích là gì?
                                        </option>
                                        <option value="Tên con vật đầu tiên của bạn?" 
                                            ${editUser.securityQuestion == 'Tên con vật đầu tiên của bạn?' ? 'selected' : ''}>
                                            Tên con vật đầu tiên của bạn?
                                        </option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="required">Câu trả lời bí mật</label>
                                    <input class="input" name="SecurityAnswer" value="${editUser.securityAnswer}" placeholder="Nhập câu trả lời..." required />
                                </div>
                            </div>
                        </div>

                        <%-- THÔNG TIN CHI TIẾT SINH VIÊN --%>
                        <c:if test="${editUser.roleId == 2}">
                            <div style="border-top: 1px solid #cbd5e1; margin-top: 25px; padding-top: 20px; background: rgba(2, 132, 199, 0.03); border-radius: 8px; padding: 15px;">
                                <h3 style="margin-bottom: 15px; color: #0284c7;">Thông tin hồ sơ sinh viên</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Họ và tên</label>
                                        <input class="input" type="text" name="FullName" value="${editUser.fullName}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Số CCCD</label>
                                        <input class="input" type="text" name="CCCD" value="${editUser.studentCode}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Ngày sinh</label>
                                        <input class="input" type="date" name="DOB" value="${editUser.dob}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Giới tính</label>
                                        <select class="input" name="Gender">
                                            <option value="Nam" ${editUser.gender == 'Nam' ? 'selected' : ''}>Nam</option>
                                            <option value="Nữ" ${editUser.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                        </select>
                                    </div>
                                    <div class="form-group" style="grid-column: span 2;">
                                        <label>Số điện thoại</label>
                                        <input class="input" type="text" name="Phone" value="${editUser.phone}" />
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <div class="form-actions">
                            <a class="btn btn-cancel" href="${pageContext.request.contextPath}/User">Huỷ</a>
                            <button class="btn btn-main" type="submit">Cập nhật dữ liệu</button>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </body>
</html>