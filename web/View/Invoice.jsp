<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lý Hóa đơn</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <div class="container main">

            <%-- BỘ LỌC NỀN GLASS TRẮNG ĐẬM --%>
            <form action="Invoice" method="GET" class="card filter-card-glass">
                <c:if test="${sessionScope.user.roleId == 1}">
                    <div class="filter-group" style="flex: 1; min-width: 150px;">
                        <label class="filter-label-bold">Phòng:</label>
                        <select name="filterRoom" class="input input-glass">
                            <option value="">-- Tất cả --</option>
                            <c:forEach items="${listRoom}" var="r">
                                <option value="${r.roomID}" ${param.filterRoom == r.roomID ? 'selected' : ''}>${r.roomNumber}</option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>

                <div class="filter-group" style="flex: 1; min-width: 120px;">
                    <label class="filter-label-bold">Tháng:</label>
                    <input type="number" name="filterMonth" value="${param.filterMonth}" min="1" max="12" class="input input-glass" placeholder="Tất cả">
                </div>

                <div class="filter-group" style="flex: 1; min-width: 150px;">
                    <label class="filter-label-bold">Trạng thái:</label>
                    <select name="filterStatus" class="input input-glass">
                        <option value="">-- Tất cả --</option>
                        <option value="Unpaid" ${param.filterStatus == 'Unpaid' ? 'selected' : ''}>Chưa thanh toán</option>
                        <option value="Paid" ${param.filterStatus == 'Paid' ? 'selected' : ''}>Đã thanh toán</option>
                    </select>
                </div>

                <div class="actions">
                    <button type="submit" class="btn-main btn-filter-search">Lọc dữ liệu</button>
                    <a href="Invoice" class="btn-secondary btn-filter-all">Xóa lọc</a>
                </div>
            </form>

            <div class="card">
                <div class="table-header-group">
                    <h2 class="page-title">
                        <c:choose>
                            <c:when test="${sessionScope.user.roleId == 1}">Quản lý Hóa đơn</c:when>
                            <c:otherwise>Hóa đơn của tôi</c:otherwise>
                        </c:choose>
                    </h2>
                    <a href="Home" class="btn-back">🏠 Về trang chủ</a>
                </div>

                <c:if test="${not empty successMsg}">
                    <div class="alert-box success-alert">${successMsg}</div>
                </c:if>

                <div class="table-wrap">
                    <table class="table-center">
                        <thead>
                            <tr>
                                <th>Mã HĐ</th>
                                <th>Phòng</th>
                                <th>Kỳ HĐ</th>
                                <th>Ngày lập</th>
                                <th>Hạn chót</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th> 
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listInvoice}" var="i">
                                <tr>
                                    <td>#${i.invoiceId}</td>
                                    <td><b class="text-primary">${i.roomNumber}</b></td>
                                    <td>${i.invoiceMonth}/${i.invoiceYear}</td>
                                    <td><fmt:formatDate value="${i.issueDate}" pattern="dd/MM/yyyy"/></td>
                                    <td><fmt:formatDate value="${i.dueDate}" pattern="dd/MM/yyyy"/></td>
                                    <td class="text-danger font-bold"><fmt:formatNumber value="${i.totalAmount}" pattern="#,###"/>đ</td>
                                    <td>
                                        <span class="badge ${i.status == 'Unpaid' ? 'badge-unpaid' : 'badge-paid'}">
                                            ${i.status == 'Unpaid' ? 'Chưa trả' : 'Đã trả'}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="actions-center">
                                            <a class="btn-table" href="RoomInfo?invoiceId=${i.invoiceId}">Chi tiết</a>
                                            <c:if test="${sessionScope.user.roleId == 1 && i.status == 'Unpaid'}">
                                                <a href="PayInvoice?id=${i.invoiceId}" class="btn-action text-success-bg">Xác nhận</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <%-- BẮT ĐẦU BLOCK PHÂN TRANG CHO HÓA ĐƠN --%>
                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <c:url value="/Invoice" var="prevUrl">
                                <c:param name="page" value="${currentPage - 1}" />
                                <c:if test="${not empty param.filterRoom}"><c:param name="filterRoom" value="${param.filterRoom}"/></c:if>
                                <c:if test="${not empty param.filterMonth}"><c:param name="filterMonth" value="${param.filterMonth}"/></c:if>
                                <c:if test="${not empty param.filterStatus}"><c:param name="filterStatus" value="${param.filterStatus}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${prevUrl}">Trước</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:url value="/Invoice" var="pageUrl">
                                <c:param name="page" value="${i}" />
                                <c:if test="${not empty param.filterRoom}"><c:param name="filterRoom" value="${param.filterRoom}"/></c:if>
                                <c:if test="${not empty param.filterMonth}"><c:param name="filterMonth" value="${param.filterMonth}"/></c:if>
                                <c:if test="${not empty param.filterStatus}"><c:param name="filterStatus" value="${param.filterStatus}"/></c:if>
                            </c:url>
                            <a class="page-btn ${currentPage == i ? 'active' : ''}" href="${pageUrl}">
                                ${i}
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <c:url value="/Invoice" var="nextUrl">
                                <c:param name="page" value="${currentPage + 1}" />
                                <c:if test="${not empty param.filterRoom}"><c:param name="filterRoom" value="${param.filterRoom}"/></c:if>
                                <c:if test="${not empty param.filterMonth}"><c:param name="filterMonth" value="${param.filterMonth}"/></c:if>
                                <c:if test="${not empty param.filterStatus}"><c:param name="filterStatus" value="${param.filterStatus}"/></c:if>
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