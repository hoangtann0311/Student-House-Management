<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lý Hợp đồng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="container main">
            <div class="card">
                <div class="table-header-group">
                    <h2 class="page-title">Danh sách Hợp đồng thuê</h2>
                    <div class="actions">
                        <form action="Contract" method="GET" class="filter-section" style="margin: 0; padding: 0; background: none; border: none;">
                            <input type="text" name="searchText" value="${searchText}" placeholder="Nhập số phòng, tên khách..." class="input" style="width: 230px;">
                            <button type="submit" class="btn-main">Tìm kiếm</button>
                            <c:if test="${not empty searchText}">
                                <a href="Contract" class="btn-secondary">Hủy</a>
                            </c:if>
                        </form>
                        <a href="CreateContract" class="btn-main" style="background: var(--success);">+ Tạo Hợp đồng</a>
                        <a href="Home" class="btn-secondary"> Trang chủ</a>
                    </div>
                </div>

                <c:if test="${not empty sessionScope.successMsg}">
                    <div class="alert-box success-alert">${sessionScope.successMsg}</div>
                    <c:remove var="successMsg" scope="session"/>
                </c:if>

                <div class="table-wrap">
                    <table class="table-center">
                        <thead>
                            <tr>
                                <th>Mã HĐ</th>
                                <th>Số Phòng</th>
                                <th>Người thuê</th>
                                <th>Bắt đầu</th>
                                <th>Kết thúc</th>
                                <th>Tiền cọc</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listContract}" var="c">
                                <tr>
                                    <td><b>#${c.contractID}</b></td>
                                    <td><span class="badge badge-info">${c.roomNumber}</span></td>
                                    <td><strong>${c.tenantName}</strong></td>
                                    <td>${c.startDate}</td>
                                    <td>${c.endDate}</td>
                                    <td><fmt:formatNumber value="${c.depositAmount}" pattern="#,###"/>đ</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.expired}">
                                                <span class="badge badge-warning">Hết hạn</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-success">Còn hạn</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="actions" style="justify-content: center;">
                                            <a href="RoomInfo?roomId=${c.roomID}" class="btn-action">Xem phòng</a>
                                            <c:if test="${c.status eq 'Active'}">

                                                <a href="TransferContract?id=${c.contractID}" class="btn-action" style="background: #8b5cf6;">Đổi người</a>
                                                <a href="TerminateContract?id=${c.contractID}&roomId=${c.roomID}" class="btn-danger" onclick="return confirm('Xác nhận dọn sạch phòng và thanh lý toàn bộ hợp đồng?')">Trả phòng</a>

                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty listContract}">
                                <tr><td colspan="8" class="text-center text-muted">Không tìm thấy hợp đồng nào.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <c:url value="/Contract" var="prevUrl">
                                <c:param name="page" value="${currentPage - 1}" />
                                <c:if test="${not empty searchText}"><c:param name="searchText" value="${searchText}"/></c:if>
                                <c:if test="${not empty status}"><c:param name="status" value="${status}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${prevUrl}">Trước</a>
                        </c:if>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:url value="/Contract" var="pageUrl">
                                <c:param name="page" value="${i}" />
                                <c:if test="${not empty searchText}"><c:param name="searchText" value="${searchText}"/></c:if>
                                <c:if test="${not empty status}"><c:param name="status" value="${status}"/></c:if>
                            </c:url>
                            <a class="page-btn ${currentPage == i ? 'active' : ''}" href="${pageUrl}">${i}</a>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <c:url value="/Contract" var="nextUrl">
                                <c:param name="page" value="${currentPage + 1}" />
                                <c:if test="${not empty searchText}"><c:param name="searchText" value="${searchText}"/></c:if>
                                <c:if test="${not empty status}"><c:param name="status" value="${status}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${nextUrl}">Sau</a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>
    </body>
</html>