<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Sửa thông tin phòng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <main class="container main">
            <div class="card" style="max-width: 800px; margin: 0 auto;">
                <div class="form-header-area">
                    <div class="title-group">
                        <h2 class="page-title">Cập nhật thông tin phòng</h2>
                        <p class="text-muted" style="margin-top: 5px;">Thay đổi dữ liệu phòng <b>${room.roomNumber}</b></p>
                    </div>
                    <a href="${pageContext.request.contextPath}/Room" class="btn-back">Trở về</a>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert-box alert-danger">${error}</div>
                </c:if>

                <div class="alert-box alert-info">
                    <span>Vui lòng nhập thông tin phòng. Các trường có dấu * là bắt buộc.</span>
                </div>

                <form action="${pageContext.request.contextPath}/EditRoomServlet" method="post">
                    <input type="hidden" name="roomID" value="${room.roomID}" />

                    <div class="form-grid">
                        <div class="form-group">
                            <label class="required">Số phòng</label>
                            <input class="input" type="text" name="roomNumber" value="${room.roomNumber}" required />
                        </div>

                        <div class="form-group">
                            <label class="required">Trạng thái</label>
                            <select class="input" name="status" required>
                                <option value="Trống" ${room.status == 'Trống' ? 'selected' : ''}>Trống</option>
                                <option value="Đang thuê" ${room.status == 'Đang thuê' ? 'selected' : ''}>Đang thuê</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="required">Sức chứa</label>
                            <input class="input" type="number" name="capacity" min="1" value="${room.capacity}" required />
                        </div>

                        <div class="form-group">
                            <label class="required">Giá thuê/Tháng</label>
                            <input class="input" type="number" name="monthlyRent" min="0" value="${room.monthlyRent}" required />
                        </div>

                        <div class="form-group">
                            <label class="required">Giá Điện (đ/kWh)</label>
                            <input class="input text-primary font-bold" type="number" name="electricPrice" min="0" value="${electricPrice}" required />
                        </div>

                        <div class="form-group">
                            <label class="required">Giá Nước (đ/m3)</label>
                            <input class="input text-success font-bold" type="number" name="waterPrice" min="0" value="${waterPrice}" required />
                        </div>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/Room" class="btn btn-cancel">Hủy</a>
                        <button type="submit" class="btn btn-main">Cập nhật</button>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>