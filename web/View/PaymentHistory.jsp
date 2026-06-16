<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Lịch sử Thanh toán</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="container main">

            <%-- BỘ LỌC ĐÃ ĐƯỢC ĐỔI SANG CARD TRẮNG KÍNH MỜ --%>
            <form action="PaymentHistory" method="GET" class="card" style="display: flex; gap: 20px; align-items: flex-end; flex-wrap: wrap; margin-bottom: 24px; padding: 20px; margin-top: 0;">
                <div class="filter-group" style="flex: 1; min-width: 150px;">
                    <label style="font-weight: 700; color: #1e293b; margin-bottom: 8px; display: block;">Doanh thu Tháng:</label>
                    <input type="number" name="month" value="${param.month}" min="1" max="12" class="input" placeholder="Tất cả" style="background: rgba(255,255,255,0.7);">
                </div>
                <div class="filter-group" style="flex: 1; min-width: 150px;">
                    <label style="font-weight: 700; color: #1e293b; margin-bottom: 8px; display: block;">Năm:</label>
                    <jsp:useBean id="now" class="java.util.Date" />
                    <fmt:formatDate var="currentYear" value="${now}" pattern="yyyy" />
                    <input type="number" name="year" value="${empty param.year ? currentYear : param.year}" class="input" placeholder="Tất cả" style="background: rgba(255,255,255,0.7);">
                </div>
                <div class="actions" style="display: flex; gap: 10px;">
                    <button type="submit" class="btn-main" style="background: #10b981; height: 42px; padding: 0 24px;">Xem doanh thu</button>
                    <a href="PaymentHistory" class="btn-secondary" style="height: 42px; display: flex; align-items: center;">Tất cả thời gian</a>
                </div>
            </form>

            <div class="revenue-grid">
                <div class="stat-card total">
                    <span class="stat-label">Tổng thực thu <c:if test="${not empty param.month}">tháng ${param.month} năm ${param.year}</c:if></span>
                    <h3 class="stat-value"><fmt:formatNumber value="${grandTotal}" pattern="#,###"/>đ</h3>
                </div>
                <div class="stat-card cash">
                    <span class="stat-label">Tiền mặt 💵</span>
                    <h3 class="stat-value text-primary"><fmt:formatNumber value="${totalCash}" pattern="#,###"/>đ</h3>
                </div>
                <div class="stat-card transfer">
                    <span class="stat-label">Chuyển khoản 💳</span>
                    <h3 class="stat-value text-danger"><fmt:formatNumber value="${totalTransfer}" pattern="#,###"/>đ</h3>
                </div>
            </div>

            <div class="card">
                <div class="table-header-group">
                    <h2 class="page-title">Lịch sử Thu tiền Hệ thống</h2>
                    <a href="${pageContext.request.contextPath}/Home" class="btn-secondary">Trở về</a>
                </div>

                <div class="table-wrap">
                    <table class="table-center">
                        <thead>
                            <tr>
                                <th>Mã GD</th>
                                <th>Phòng</th>
                                <th>Kỳ hóa đơn</th>
                                <th>Số tiền đã trả</th>
                                <th>Ngày giờ thanh toán</th>
                                <th>Phương thức</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listPayment}" var="p">
                                <tr>
                                    <td>#${p.paymentId}</td>
                                    <td><b class="text-primary">${p.roomNumber}</b></td>
                                    <td>Tháng ${p.month}/${p.year}</td>
                                    <td class="text-success font-bold">+ <fmt:formatNumber value="${p.amount}" pattern="#,###"/>đ</td>
                                    <td><fmt:formatDate value="${p.paymentDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                    <td>
                                        <span class="badge-method ${p.paymentMethod == 'Tiền mặt' ? 'cash' : 'transfer'}">
                                            ${p.paymentMethod}
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty listPayment}">
                                <tr><td colspan="6" class="text-muted">Chưa có giao dịch thanh toán nào trong thời gian này.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>