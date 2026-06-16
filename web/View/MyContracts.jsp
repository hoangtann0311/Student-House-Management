<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Hợp đồng của tôi</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="container main">
            <div class="card">
                <div class="table-header-group">
                    <h2 class="page-title">Hợp đồng của tôi</h2>
                    <div class="actions">
                        <a href="${pageContext.request.contextPath}/Home" class="btn-secondary">Về Trang chủ</a>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty myContracts}">
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
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${myContracts}" var="c">
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
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="text-align: center; padding: 40px;">
                            <h3 class="text-muted">Chưa có hợp đồng</h3>
                            <p>Bạn hiện chưa thuê phòng nào. Vui lòng liên hệ Admin để được hỗ trợ làm hợp đồng.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </body>
</html>