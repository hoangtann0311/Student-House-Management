<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Tạo Hợp đồng mới</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" /> 
        <div class="container main">

            <%-- Thẻ card giờ đã dùng thêm class contract-card --%>
            <div class="card contract-card">

                <div class="form-header-area">
                    <div class="title-group">
                        <h2 class="page-title">Tạo Hợp đồng mới</h2>
                    </div>
                    <a href="${pageContext.request.contextPath}/Contract" class="btn-back">Hủy và Trở về</a>
                </div>

                <h3 class="section-title">Điền thông tin Hợp đồng</h3>
                <p class="text-danger form-note">
                    <i>Chú ý: Các trường có dấu <span class="req">*</span> là bắt buộc phải điền đầy đủ.</i>
                </p>

                <form action="${pageContext.request.contextPath}/CreateContract" method="POST">
                    <div class="form-row-lg"> 
                        <div class="form-group">
                            <label class="required">Tài khoản đăng nhập của Sinh viên</label>
                            <select name="userId" required class="input">
                                <option value="">-- Chọn sinh viên --</option>
                                <c:forEach items="${listStudent}" var="s"> 
                                    <option value="${s.userId}">${s.fullName} - CCCD: ${s.studentCode} (${s.username})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="required">Chọn phòng muốn thuê (Phòng đơn / Phòng ghép)</label>
                            <select name="roomId" required class="input">
                                <option value="">-- Chọn một phòng --</option>
                                <optgroup label="PHÒNG ĐƠN (1 Người)">
                                    <c:forEach items="${listRoom}" var="room">
                                        <c:if test="${room.capacity == 1}">
                                            <option value="${room.roomID}">Phòng ${room.roomNumber} - Giá: ${String.format("%,d", room.monthlyRent)}đ/tháng</option>
                                        </c:if>
                                    </c:forEach>
                                </optgroup>
                                <optgroup label="PHÒNG GHÉP (2 - 3 Người) - Có thể ở ghép">
                                    <c:forEach items="${listRoom}" var="room">
                                        <c:if test="${room.capacity > 1}">
                                            <option value="${room.roomID}">Phòng ${room.roomNumber} (Đã ở: ${room.currentOccupants}/${room.capacity} người) - Giá: ${String.format("%,d", room.monthlyRent)}đ/tháng</option>
                                        </c:if>
                                    </c:forEach>
                                </optgroup>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Tiền đặt cọc (VNĐ) <span class="text-danger">*</span></label>
                            <input type="number" name="depositAmount" placeholder="VD: 2000000" class="input">
                            <small>(Chỉ bắt buộc nhập nếu thuê phòng trống. Ở ghép có thể bỏ qua)</small>
                        </div>

                        <div class="form-group">
                            <label>Số tháng muốn thuê <span class="text-danger">*</span></label>
                            <select name="months" class="input">
                                <option value="">-- Chọn thời hạn hợp đồng --</option>
                                <option value="3">3 Tháng</option>
                                <option value="6">6 Tháng</option>
                                <option value="12">12 Tháng</option>
                            </select>
                            <small>(Chỉ bắt buộc nhập nếu thuê phòng trống)</small>
                        </div>
                    </div>

                    <%-- Đường phân cách --%>
                    <div class="form-divider">
                        <h3 class="section-title highlight">⚡ Bảng giá dịch vụ áp dụng</h3>
                        <p class="text-danger form-note">
                            <i>(Lưu ý: Đối với Phòng ghép đã có người ở, hệ thống sẽ tự động đồng bộ theo giá điện nước của người trước đó bất kể bạn nhập số gì ở dưới đây)</i>
                        </p>

                        <div class="form-grid">
                            <div class="form-group">
                                <label class="required">Giá Điện (đ/kWh)</label>
                                <input type="number" name="electricPrice" value="3500" placeholder="VD: 3500" class="input">
                            </div>
                            <div class="form-group">
                                <label class="required">Giá Nước (đ/m3)</label>
                                <input type="number" name="waterPrice" value="15000" placeholder="VD: 15000" class="input">
                            </div>
                            <div class="form-group">
                                <label>Wifi (đ/tháng) - Cố định</label>
                                <input type="number" name="wifiPrice" value="100000" readonly class="input">
                            </div>
                            <div class="form-group">
                                <label>Tiền Rác (đ/tháng) - Cố định</label>
                                <input type="number" name="garbagePrice" value="50000" readonly class="input">
                            </div>
                        </div>
                    </div>

                    <%-- Nút Submit --%>
                    <div class="submit-area">
                        <button type="submit" class="btn-submit-large">Tạo Hợp Đồng Thuê Ngay</button>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>