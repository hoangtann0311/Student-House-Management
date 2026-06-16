<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Hồ sơ Chi tiết Phòng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <div class="container main">
            <div class="card">
                <%-- Tiêu đề và nút quay lại --%>
                <div class="form-header-area">
                    <div class="title-group">
                        <h2 class="page-title">Hồ sơ chi tiết - Phòng ${roomInfo.roomNumber}</h2>
                    </div>
                    <a href="javascript:history.back()" class="btn-back">← Trở về</a>
                </div>

                <%-- Khối thông tin chung (Sử dụng Grid) --%>
                <div class="grid-auto-fit">

                    <%-- 1. Thông tin Phòng --%>
                    <div class="card info-card">
                        <h3 class="text-primary font-bold">🏠 Thông tin Phòng</h3>
                        <p><b>Số phòng:</b> ${roomInfo.roomNumber}</p>
                        <p><b>Sức chứa:</b> ${roomInfo.capacity} người</p>
                        <p><b>Giá thuê:</b> <strong class="text-danger"><fmt:formatNumber value="${roomInfo.monthlyRent}" pattern="#,###"/>đ/tháng</strong></p>
                    </div>

                    <%-- 2. Bảng giá dịch vụ --%>
                    <div class="card info-card">
                        <h3 class="font-bold" style="color: #8b5cf6;">⚡ Bảng giá dịch vụ</h3>
                        <ul style="padding-left: 20px;">
                            <c:forEach items="${unitPrices}" var="price">
                                <c:if test="${!fn:containsIgnoreCase(price.description, 'Room Rent') && !fn:containsIgnoreCase(price.description, 'Phòng')}">
                                    <c:set var="unit" value="/tháng" />
                                    <c:choose>
                                        <c:when test="${fn:containsIgnoreCase(price.description, 'Electricity') || fn:containsIgnoreCase(price.description, 'Điện')}">
                                            <c:set var="unit" value="/kWh" />
                                        </c:when>
                                        <c:when test="${fn:containsIgnoreCase(price.description, 'Water') || fn:containsIgnoreCase(price.description, 'Nước')}">
                                            <c:set var="unit" value="/m3" />
                                        </c:when>
                                    </c:choose>
                                    <li style="margin-bottom: 5px;">
                                        <b>${price.description}:</b> <fmt:formatNumber value="${price.unitPrice}" pattern="#,###"/>đ<span class="text-muted" style="font-size: 0.85em;">${unit}</span>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>

                    <%-- 3. Danh sách người thuê --%>
                    <div class="card info-card">
                        <h3 class="text-success font-bold">👥 Người đang ở</h3>
                        <c:choose>
                            <c:when test="${isRoomEmpty}">
                                <p class="text-muted italic">Phòng hiện đang trống</p>
                            </c:when>
                            <c:otherwise>
                                <%-- Tìm đến phần hiển thị danh sách người đang ở và sửa lại như sau --%>
                                <c:forEach items="${listStudents}" var="st" varStatus="loop">
                                    <div style="${!loop.last ? 'border-bottom: 1px dashed #cbd5e1; margin-bottom: 10px; padding-bottom: 5px;' : ''}">
                                        <p style="margin: 0 0 5px 0;"><b>Họ Tên:</b> ${st.fullName}</p>

                                        <%-- DÒNG MỚI THÊM: Hiển thị tên tài khoản --%>
                                        <p style="margin: 0 0 5px 0;"><b>Tài khoản:</b> <span class="badge-info" style="padding: 2px 8px; font-size: 12px;">${st.username}</span></p>

                                        <c:if test="${sessionScope.user.roleId == 1}">
                                            <p style="margin: 0 0 5px 0;"><b>CCCD:</b> ${st.studentCode}</p>
                                        </c:if>
                                        <p style="margin: 0;"><b>SĐT:</b> ${st.phone}</p>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <%-- Khối Chi tiết Hóa đơn (Chỉ hiện khi xem từ menu Hóa đơn) --%>
                <c:if test="${viewInvoiceDetail}">
                    <div class="table-wrap" style="margin-top: 30px; border: 2px solid var(--primary);">
                        <h3 class="text-primary font-bold" style="padding: 15px 15px 0 15px;">🔍 Chi tiết phí Hóa đơn #${invoiceDetail.invoiceId}</h3>
                        <table>
                            <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Nội dung</th>
                                    <th>Số lượng</th>
                                    <th>Đơn giá</th>
                                    <th class="text-right">Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${items}" var="item" varStatus="loop">
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td><b>${item.description}</b></td>
                                        <td>${item.quantity}</td>
                                        <td><fmt:formatNumber value="${item.unitPrice}" pattern="#,###"/></td>
                                        <td class="text-right font-bold"><fmt:formatNumber value="${item.amount}" pattern="#,###"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr style="background: #f8fafc;">
                                    <td colspan="4" class="text-right"><b>Tổng cộng:</b></td>
                                    <td class="text-danger font-bold text-right" style="font-size: 18px;"><fmt:formatNumber value="${invoiceDetail.totalAmount}" pattern="#,###"/>đ</td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>

                    <%-- Khối Thanh toán QR cho Sinh viên (Sử dụng CSS Class trong forms.css) --%>
                    <c:if test="${sessionScope.user.roleId != 1 && invoiceDetail.status == 'Unpaid'}">
                        <div class="payment-wrapper">
                            <details class="payment-details">
                                <summary>💳 Thanh toán hóa đơn này</summary>

                                <div class="payment-instruction-card">
                                    <h3 class="text-primary font-bold">Hướng dẫn thanh toán trực tuyến</h3>
                                    <p class="text-muted">Quét mã QR dưới đây để tự động nhập thông tin chuyển khoản</p>

                                    <c:set var="bankID" value="MB" />
                                    <c:set var="accountNo" value="0396219429" />
                                    <c:set var="accountName" value="HOANG VAN TAN" />
                                    <c:set var="description" value="Thanh toan phong ${roomInfo.roomNumber} thang ${invoiceDetail.invoiceMonth}" />
                                    <fmt:formatNumber var="amountStr" value="${invoiceDetail.totalAmount}" pattern="0" />

                                    <img src="https://img.vietqr.io/image/${bankID}-${accountNo}-compact2.png?amount=${amountStr}&addInfo=${description}&accountName=${accountName}" 
                                         alt="QR Thanh toán" class="qr-code-img">

                                    <div class="payment-info-box">
                                        <p style="margin: 0;">Ngân hàng: <b>MB Bank</b></p>
                                        <p style="margin: 0;">Số tài khoản: <b class="text-primary" style="font-size: 18px;">${accountNo}</b></p>
                                        <p style="margin: 0;">Chủ tài khoản: <b>${accountName}</b></p>
                                        <p style="margin: 0;">Số tiền: <b class="text-danger"><fmt:formatNumber value="${invoiceDetail.totalAmount}" pattern="#,###"/>đ</b></p>
                                        <p style="margin: 0;">Nội dung: <b class="text-danger">${description}</b></p>
                                        <p style="margin-top: 15px; font-size: 13px;" class="text-muted">
                                            <i>* Lưu ý: Sau khi chuyển khoản thành công, Admin sẽ kiểm tra và cập nhật trạng thái hóa đơn của bạn.</i>
                                        </p>
                                    </div>
                                </div>
                            </details>
                        </div>
                    </c:if>
                </c:if>
            </div>
        </div>
    </body>
</html>