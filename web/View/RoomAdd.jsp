<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Thêm phòng mới</title>
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
                        <h2 class="page-title">Thêm Phòng Trọ Mới</h2>
                    </div>
                    <a href="${pageContext.request.contextPath}/Room" class="btn-back">Trở về</a>
                </div>

                <% String error = (String) request.getAttribute("error"); %>
                <% if (error != null) { %>
                    <div class="alert-box alert-danger"><%= error %></div>
                <% } %>

                <div class="alert-box alert-info">
                    <span>Vui lòng nhập thông tin phòng. Các trường có dấu * là bắt buộc.</span>
                </div>

                <form action="${pageContext.request.contextPath}/AddRoomServlet" method="post">
                    <div class="form-grid">
                        <div class="form-group">
                            <label class="required">Số phòng (Room Number)</label>
                            <input class="input" type="text" name="roomNumber" required />
                            <small>Mã phòng dễ nhớ (A1xx, B1xx, C1xx,...).</small>
                        </div>

                        <div class="form-group">
                            <label class="required">Trạng thái (Status)</label>
                            <select class="input" name="status" required>
                                <option value="Trống" selected>Trống</option>
                                <option value="Đang thuê">Đang thuê</option>
                            </select>
                            <small>Trạng thái hiện tại của phòng.</small>
                        </div>

                        <div class="form-group">
                            <label class="required">Sức chứa (Capacity)</label>
                            <input class="input" type="number" name="capacity" min="1" required />
                            <small>Số người tối đa.</small>
                        </div>

                        <div class="form-group">
                            <label class="required">Giá thuê/Tháng (Monthly Rent)</label>
                            <input class="input" type="number" name="monthlyRent" min="0" required />
                            <small>Tiền thuê mỗi tháng (VNĐ).</small>
                        </div>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/Room" class="btn btn-cancel">Hủy</a>
                        <button type="submit" class="btn btn-main">Lưu Phòng</button>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>