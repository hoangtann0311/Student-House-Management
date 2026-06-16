<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lý Điện Nước</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="container main">

            <div class="card">
                <div class="table-header-group">
                    <h2 class="page-title">Nhập chỉ số hàng loạt</h2>
                    <a href="${pageContext.request.contextPath}/Home" class="btn-secondary">← Trang chủ</a>
                </div>

                <c:if test="${not empty errorMsg}"><div class="alert-box alert-danger">${errorMsg}</div></c:if>
                <c:if test="${not empty successMsg}"><div class="alert-box success-alert">${successMsg}</div></c:if>

                <form action="MeterReading" method="POST">
                    <%-- BỐ CỤC MỚI: GỌN GÀNG THEO CHIỀU NGANG --%>
                    <div class="filter-card-glass" style="display: flex; align-items: center; gap: 15px; padding: 15px; margin-bottom: 20px;">
                        <label class="font-bold" style="white-space: nowrap; margin-bottom: 0;">Kỳ ghi chỉ số:</label>
                        
                        <div style="display: flex; align-items: center; gap: 8px;">
                            <span class="text-muted">Tháng</span>
                            <input type="number" name="month" value="${batchMonth}" min="1" max="12" required class="input" style="width: 70px; background: rgba(255,255,255,0.8);"> 
                            
                            <span class="font-bold">/</span>
                            
                            <span class="text-muted">Năm</span>
                            <input type="number" name="year" value="${batchYear}" required class="input" style="width: 90px; background: rgba(255,255,255,0.8);">
                        </div>

                        <div class="text-muted" style="font-size: 13px; font-style: italic; margin-left: auto;">
                            (Phòng để trống sẽ được hệ thống tự động bỏ qua)
                        </div>
                    </div>

                    <div class="table-wrap">
                        <table class="table-center">
                            <thead>
                                <tr>
                                    <th>Phòng</th>
                                    <th>Chỉ số tháng cũ</th>
                                    <th>Điện cũ</th>
                                    <th class="text-primary">Điện mới</th>
                                    <th>Nước Cũ</th>
                                    <th class="text-primary">Nước mới</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${listActiveRooms}" var="room">
                                    <tr style="${room.readingId == 1 ? 'background-color: #f1f5f9; opacity: 0.6;' : ''}">
                                        <td>
                                            <strong style="font-size: 16px;">${room.roomNumber}</strong>
                                            <c:if test="${room.readingId == 1}">
                                                <br><span class="text-success font-bold" style="font-size: 11px;">✔ ĐÃ XONG</span>
                                            </c:if>
                                            <input type="hidden" name="roomId[]" value="${room.roomId}">
                                            <input type="hidden" name="roomNum[]" value="${room.roomNumber}">
                                        </td>
                                        <td class="text-muted">${room.readingMonth}/${room.readingYear}</td>
                                        <td class="font-bold">${room.electricityIndex}</td>
                                        <td>
                                            <input type="number" name="electricityIndex[]" min="${room.electricityIndex}" class="input" placeholder="${room.readingId == 1 ? 'Đã nhập' : 'Số mới...'}" ${room.readingId == 1 ? 'readonly' : ''} style="width: 100px; text-align: center;">
                                        </td>
                                        <td class="font-bold">${room.waterIndex}</td>
                                        <td>
                                            <input type="number" name="waterIndex[]" min="${room.waterIndex}" class="input" placeholder="${room.readingId == 1 ? 'Đã nhập' : 'Số mới...'}" ${room.readingId == 1 ? 'readonly' : ''} style="width: 100px; text-align: center;">
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div style="display: flex; justify-content: flex-end; margin-top: 15px;">
                        <button type="submit" class="btn-main" style="padding: 10px 30px;">Lưu tất cả chỉ số</button>
                    </div>
                </form>
            </div>

            <%-- PHẦN LỊCH SỬ BÊN DƯỚI (GIỮ NGUYÊN BỐ CỤC CŨ NHƯNG ÁP DỤNG CLASS MỚI) --%>
            <div class="card" style="margin-top: 25px;">
                <div class="table-header-group">
                    <h2 class="page-title">Quản lý chỉ số Điện Nước</h2>
                </div>
                
                <form action="MeterReading" method="GET" class="filter-card-glass" style="display: flex; gap: 15px; align-items: flex-end; flex-wrap: wrap; padding: 15px; margin-bottom: 20px;">
                    <div class="filter-group">
                        <label class="font-bold">Phòng:</label>
                        <select name="filterRoom" class="input">
                            <option value="">-- Tất cả --</option>
                            <c:forEach items="${listRoom}" var="r">
                                <option value="${r.roomID}" ${param.filterRoom == r.roomID ? 'selected' : ''}>${r.roomNumber}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label class="font-bold">Tháng:</label>
                        <input type="number" name="filterMonth" value="${param.filterMonth}" min="1" max="12" class="input" placeholder="Tất cả" style="width: 80px;">
                    </div>
                    <div class="filter-group">
                        <label class="font-bold">Năm:</label>
                        <input type="number" name="filterYear" value="${param.filterYear}" class="input" placeholder="Tất cả" style="width: 100px;">
                    </div>
                    <div class="actions">
                        <button type="submit" class="btn-main">Lọc dữ liệu</button>
                        <a href="MeterReading" class="btn-secondary">Xóa lọc</a>
                    </div>
                </form>

                <div class="table-wrap">
                    <table class="table-center">
                        <thead>
                            <tr>
                                <th>Phòng</th>
                                <th>Tháng/Năm</th>
                                <th>Số điện đã dùng (kWh)</th>
                                <th>Số nước đã dùng (m3)</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listMeter}" var="m">
                                <tr>
                                    <td><b>${m.roomNumber}</b></td>
                                    <td>${m.readingMonth} / ${m.readingYear}</td>
                                    <td class="text-primary font-bold">${m.electricityIndex}</td>
                                    <td class="text-success font-bold">${m.waterIndex}</td>
                                    <td>
                                        <a href="DeleteMeter?id=${m.readingId}" class="btn-danger" onclick="return confirm('CẢNH BÁO: Xóa chỉ số này sẽ xóa sạch Hóa đơn tương ứng?')">Xóa</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:url value="/MeterReading" var="pageUrl">
                                <c:param name="page" value="${i}" />
                                <c:if test="${not empty searchKeyword}"><c:param name="search" value="${searchKeyword}"/></c:if>
                            </c:url>
                            <a class="page-btn ${currentPage == i ? 'active' : ''}" href="${pageUrl}">${i}</a>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
    </body>
</html>