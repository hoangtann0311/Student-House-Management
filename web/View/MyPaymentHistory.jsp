<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Lịch sử thanh toán của tôi</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="container main">

            <%-- BỘ LỌC THÁNG/NĂM --%>
            <form action="MyPaymentHistory" method="GET" class="card filter-form">
                <div class="filter-item">
                    <label class="filter-label">Kỳ hóa đơn (Tháng):</label>
                    <input type="number" name="month" value="${selectedMonth}" min="1" max="12" class="input" placeholder="Tất cả các tháng">
                </div>
                <div class="filter-item">
                    <label class="filter-label">Năm:</label>
                    <jsp:useBean id="now" class="java.util.Date" />
                    <fmt:formatDate var="currentYear" value="${now}" pattern="yyyy" />
                    <input type="number" name="year" value="${empty selectedYear ? currentYear : selectedYear}" class="input" placeholder="Năm">
                </div>
                <div class="filter-actions">
                    <button type="submit" class="btn-filter-submit">Xem chi tiết</button>
                    <a href="MyPaymentHistory" class="btn-secondary btn-filter-reset">Xem tất cả</a>
                </div>
            </form>

            <%-- HIỂN THỊ TỔNG TIỀN ĐÃ TRẢ --%>
            <div class="card total-summary-card">
                <span class="stat-label">
                    Tổng tiền bạn đã thanh toán 
                    <c:if test="${not empty selectedMonth}">tháng ${selectedMonth}</c:if>
                    <c:if test="${not empty selectedYear}"> năm ${selectedYear}</c:if>
                </span>
                <h2 class="stat-value text-success total-value-large">
                    <fmt:formatNumber value="${totalPaid}" pattern="#,###"/>đ
                </h2>
            </div>

            <div class="card">
                <div class="table-header-group">
                    <h2 class="page-title">Chi tiết lịch sử giao dịch</h2>
                    <a href="${pageContext.request.contextPath}/Home" class="btn-secondary">Trở về Trang chủ</a>
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
                                <tr><td colspan="6" class="text-muted">Bạn chưa có giao dịch thanh toán nào.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                <%-- BẮT ĐẦU BLOCK PHÂN TRANG CHO LỊCH SỬ THANH TOÁN --%>
                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <c:url value="/MyPaymentHistory" var="prevUrl">
                                <c:param name="page" value="${currentPage - 1}" />
                                <c:if test="${not empty param.month}"><c:param name="month" value="${param.month}"/></c:if>
                                <c:if test="${not empty param.year}"><c:param name="year" value="${param.year}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${prevUrl}">Trước</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:url value="/MyPaymentHistory" var="pageUrl">
                                <c:param name="page" value="${i}" />
                                <c:if test="${not empty param.month}"><c:param name="month" value="${param.month}"/></c:if>
                                <c:if test="${not empty param.year}"><c:param name="year" value="${param.year}"/></c:if>
                            </c:url>
                            <a class="page-btn ${currentPage == i ? 'active' : ''}" href="${pageUrl}">
                                ${i}
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <c:url value="/MyPaymentHistory" var="nextUrl">
                                <c:param name="page" value="${currentPage + 1}" />
                                <c:if test="${not empty param.month}"><c:param name="month" value="${param.month}"/></c:if>
                                <c:if test="${not empty param.year}"><c:param name="year" value="${param.year}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${nextUrl}">Sau</a>
                        </c:if>
                    </div>
                </c:if>
                <%-- KẾT THÚC BLOCK PHÂN TRANG --%>
            </div>
        </div>
    </body>
</html>