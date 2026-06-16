<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Rooms Management</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body>
        <jsp:include page="common/header-auth.jsp" />

        <main class="container main">
            <div class="card">
                <div class="room-header">
                    <h2>Danh sách phòng</h2>
                    <div class="room-actions">
                        <a href="${pageContext.request.contextPath}/AddRoomServlet" class="btn-main">+ Thêm phòng</a>
                        <a href="${pageContext.request.contextPath}/Room" class="btn-secondary">Làm mới</a>
                        <a href="${pageContext.request.contextPath}/Home" class="btn-secondary">← Trang chủ</a>
                    </div>
                </div>

                <div class="room-search">
                    <form action="${pageContext.request.contextPath}/Room" method="GET" class="filter-section filter-card-glass">
                        <div class="filter-group">
                            <label>Số phòng</label>
                            <input class="input input-glass" name="roomNumber" type="text" value="${param.roomNumber}" placeholder="VD: A101" style="width: 120px;" />
                        </div>

                        <div class="filter-group">
                            <label>Sức chứa</label>
                            <input class="input input-glass" name="capacity" type="number" min="1" value="${param.capacity}" placeholder="Số người" style="width: 100px;" />
                        </div>

                        <div class="filter-group">
                            <label>Giá thuê (VNĐ)</label>
                            <div style="display: flex; gap: 5px; align-items: center;">
                                <input class="input input-glass" name="minPrice" type="number" value="${param.minPrice}" placeholder="Từ..." style="width: 110px;" />
                                <span>-</span>
                                <input class="input input-glass" name="maxPrice" type="number" value="${param.maxPrice}" placeholder="Đến..." style="width: 110px;" />
                            </div>
                        </div>

                        <div class="filter-group">
                            <label>Trạng thái</label>
                            <select name="status" class="input input-glass" style="width: 130px;">
                                <option value="">-- Tất cả --</option>
                                <option value="Trống" ${param.status == 'Trống' ? 'selected' : ''}>Trống</option>
                                <option value="Đang thuê" ${param.status == 'Đang thuê' ? 'selected' : ''}>Đang thuê</option>
                                <option value="Đã cọc" ${param.status == 'Đã cọc' ? 'selected' : ''}>Đã cọc</option>
                                <option value="Đang bảo trì" ${param.status == 'Đang bảo trì' ? 'selected' : ''}>Đang bảo trì</option>
                            </select>
                        </div>

                        <div class="filter-group" style="flex-direction: row; align-items: flex-end;">
                            <button type="submit" class="btn-main btn-filter-search" style="padding: 10px 20px; border: none; cursor: pointer;">Tìm kiếm</button>
                            <a href="${pageContext.request.contextPath}/Room" class="btn-secondary" style="padding: 10px 16px; text-decoration: none;">Bỏ lọc</a>
                        </div>
                    </form>
                </div>

                <c:if test="${not empty sessionScope.errorMsg}">
                    <div class="alert-box alert-danger">${sessionScope.errorMsg}</div>
                    <c:remove var="errorMsg" scope="session" />
                </c:if>
                
                <c:if test="${not empty sessionScope.successMsg}">
                    <div class="alert-box alert-success">${sessionScope.successMsg}</div>
                    <c:remove var="successMsg" scope="session" />
                </c:if>

                <div class="text-muted font-bold" style="margin-bottom: 15px;">
                    Tổng số phòng: <c:out value="${totalRoomsCount}"/>
                </div>

                <div class="table-wrap">
                    <table class="table-center" id="roomsTable">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Số phòng</th>
                                <th>Sức chứa</th>
                                <th>Giá thuê/Tháng</th>
                                <th>Trạng thái</th>
                                <th class="text-right">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty rooms}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted">Không tìm thấy phòng nào.</td>
                                </tr>
                            </c:if>

                            <c:forEach items="${rooms}" var="r">
                                <tr>
                                    <td><strong>${r.roomID}</strong></td>
                                    <td><strong>${r.roomNumber}</strong></td>
                                    <td>${r.capacity}</td>
                                    <td><fmt:formatNumber value="${r.monthlyRent}" pattern="#,###"/>đ</td>
                                    <td>
                                        <c:set var="st" value="${r.status}" />
                                        <c:choose>
                                            <c:when test="${st eq 'Trống'}"><span class="badge available">Trống</span></c:when>
                                            <c:when test="${st eq 'Đang thuê'}"><span class="badge occupied">Đang thuê</span></c:when>
                                            <c:when test="${st eq 'Đã cọc'}"><span class="badge badge-info">Đã cọc</span></c:when>
                                            <c:otherwise><span class="badge maintenance">Đang bảo trì</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="actions" style="justify-content: flex-end;">
                                            <a class="btn-action" href="${pageContext.request.contextPath}/RoomInfo?roomId=${r.roomID}">Chi tiết</a>
                                            <a class="btn-action" href="${pageContext.request.contextPath}/EditRoomServlet?id=${r.roomID}">Sửa</a>
                                            <form action="${pageContext.request.contextPath}/DeleteRoomServlet" method="post" style="margin: 0;">
                                                <input type="hidden" name="id" value="${r.roomID}" />
                                                <button type="submit" class="btn-danger" onclick="return confirm('Bạn chắc chắn muốn xóa phòng ${r.roomNumber} không?');">Xóa</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <c:url value="/Room" var="prevUrl">
                                <c:param name="page" value="${currentPage - 1}" />
                                <c:if test="${not empty param.roomNumber}"><c:param name="roomNumber" value="${param.roomNumber}"/></c:if>
                                <c:if test="${not empty param.capacity}"><c:param name="capacity" value="${param.capacity}"/></c:if>
                                <c:if test="${not empty param.minPrice}"><c:param name="minPrice" value="${param.minPrice}"/></c:if>
                                <c:if test="${not empty param.maxPrice}"><c:param name="maxPrice" value="${param.maxPrice}"/></c:if>
                                <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${prevUrl}">Trở về</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:url value="/Room" var="pageUrl">
                                <c:param name="page" value="${i}" />
                                <c:if test="${not empty param.roomNumber}"><c:param name="roomNumber" value="${param.roomNumber}"/></c:if>
                                <c:if test="${not empty param.capacity}"><c:param name="capacity" value="${param.capacity}"/></c:if>
                                <c:if test="${not empty param.minPrice}"><c:param name="minPrice" value="${param.minPrice}"/></c:if>
                                <c:if test="${not empty param.maxPrice}"><c:param name="maxPrice" value="${param.maxPrice}"/></c:if>
                                <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}"/></c:if>
                            </c:url>
                            <a class="page-btn ${currentPage == i ? 'active' : ''}" href="${pageUrl}">${i}</a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <c:url value="/Room" var="nextUrl">
                                <c:param name="page" value="${currentPage + 1}" />
                                <c:if test="${not empty param.roomNumber}"><c:param name="roomNumber" value="${param.roomNumber}"/></c:if>
                                <c:if test="${not empty param.capacity}"><c:param name="capacity" value="${param.capacity}"/></c:if>
                                <c:if test="${not empty param.minPrice}"><c:param name="minPrice" value="${param.minPrice}"/></c:if>
                                <c:if test="${not empty param.maxPrice}"><c:param name="maxPrice" value="${param.maxPrice}"/></c:if>
                                <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${nextUrl}">Tiếp theo</a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </main>
    </body>
</html>