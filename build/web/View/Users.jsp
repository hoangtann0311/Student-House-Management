<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Users Management</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <main class="container main">
            <div class="card">

                <div class="table-header-group">
                    <h2 class="page-title">Danh sách tài khoản</h2>

                    <div class="actions">
                        <c:if test="${sessionScope.user.roleId == 1}">
                            <a class="btn-main" href="${pageContext.request.contextPath}/UserCreate">
                                + Tạo tài khoản
                            </a>
                        </c:if>
                        <a class="btn-secondary" href="${pageContext.request.contextPath}/User">
                            Làm mới
                        </a>
                        <a class="btn-secondary" href="${pageContext.request.contextPath}/Home">
                            ← Trang chủ
                        </a>
                    </div>
                </div>

                <c:if test="${not empty sessionScope.errorMsg}">
                    <div class="alert-danger" style="margin-bottom: 20px;">
                        ${sessionScope.errorMsg}
                    </div>
                    <c:remove var="errorMsg" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.successMsg}">
                    <div class="success-alert" style="margin-bottom: 20px;">
                        ${sessionScope.successMsg}
                    </div>
                    <c:remove var="successMsg" scope="session" />
                </c:if>

                <div class="room-search">
                    <form action="${pageContext.request.contextPath}/User" method="GET">
                        <input class="input" id="userSearch" name="searchText" type="text" value="${searchText}" placeholder="Tìm kiếm theo Username..." />
                    </form>
                </div>

                <div class="table-wrap">
                    <table class="table-center" id="usersTable">
                        <thead>
                            <tr>
                                <th>UserID</th>
                                <th>Username</th>
                                <th>Full Name</th>
                                <th>Role</th>
                                <th>Trạng thái ở</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:if test="${empty users}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted"> 
                                        Không tìm thấy tài khoản nào.
                                    </td>
                                </tr>
                            </c:if>

                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td><strong>${user.userId}</strong></td>
                                    <td><strong>${user.username}</strong></td>
                                    
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty user.fullName}">
                                                ${user.fullName}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted italic" style="font-size: 0.9em;">(Chưa cập nhật)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${user.roleId == 1}">
                                                <span class="badge maintenance">Admin</span>
                                            </c:when>
                                            <c:when test="${user.roleId == 2}">
                                                <span class="badge available">Student</span>
                                            </c:when>                                           
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${user.roleId == 1}">
                                                <span class="text-muted">--</span>
                                            </c:when>
                                            <c:when test="${user.renting}">
                                                <span class="badge available" style="background: #dcfce7; color: #166534; border: 1px solid #bbf7d0;">Đang ở phòng</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge" style="background: #fff7ed; color: #c2410c; border: 1px solid #fdba74;">Chưa thuê phòng</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <div class="actions" style="justify-content:center;">                                        
                                            <a class="btn-action" href="${pageContext.request.contextPath}/DetailUser?id=${user.userId}">Chi tiết</a>
                                            <a class="btn-action" href="${pageContext.request.contextPath}/EditUser?id=${user.userId}">Sửa</a>
                                            <a class="btn-danger" href="${pageContext.request.contextPath}/DeleteUser?id=${user.userId}" 
                                               onclick="return confirm('Bạn chắc chắn muốn xoá tài khoản ${user.username} không?');">Xoá</a>
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
                            <c:url value="/User" var="prevUrl">
                                <c:param name="page" value="${currentPage - 1}" />
                                <c:if test="${not empty searchText}"><c:param name="searchText" value="${searchText}"/></c:if>
                                <c:if test="${not empty roleId}"><c:param name="roleId" value="${roleId}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${prevUrl}">Trước</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:url value="/User" var="pageUrl">
                                <c:param name="page" value="${i}" />
                                <c:if test="${not empty searchText}"><c:param name="searchText" value="${searchText}"/></c:if>
                                <c:if test="${not empty roleId}"><c:param name="roleId" value="${roleId}"/></c:if>
                            </c:url>
                            <a class="page-btn ${currentPage == i ? 'active' : ''}" href="${pageUrl}">
                                ${i}
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <c:url value="/User" var="nextUrl">
                                <c:param name="page" value="${currentPage + 1}" />
                                <c:if test="${not empty searchText}"><c:param name="searchText" value="${searchText}"/></c:if>
                                <c:if test="${not empty roleId}"><c:param name="roleId" value="${roleId}"/></c:if>
                            </c:url>
                            <a class="page-btn nav-btn" href="${nextUrl}">Sau</a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </main>
    </body>
</html>