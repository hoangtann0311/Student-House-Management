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
                
                <div class="form-header-area">
                    <div class="title-group">
                        <h2 class="page-title">Hồ sơ chi tiết - Phòng ${roomInfo.roomNumber}</h2>
                    </div>
                    <a href="javascript:history.back()" class="btn-back">← Trở về</a>
                </div>

                <div class="grid-auto-fit">
                    <%-- Thẻ 1: Thông tin Phòng --%>
                    <div class="card info-card">
                        <h3 class="text-primary" style="margin-top: 0; margin-bottom: 10px;">🏠 Thông tin Phòng</h3>
                        <p><b>Số phòng:</b> ${roomInfo.roomNumber}</p>
                        <p><b>Sức chứa:</b> ${roomInfo.capacity} người</p>
                        <p><b>Giá thuê:</b> <strong class="text-danger"><fmt:formatNumber value="${roomInfo.monthlyRent}" pattern="#,###"/>đ/tháng</strong></p>
                    </div>

                    <%-- Thẻ 2: Bảng giá dịch vụ --%>
                    <div class="card info-card">
                        <h3 style="margin-top: 0; margin-bottom: 10px; color: #8b5cf6;">⚡ Bảng giá dịch vụ</h3>
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

                    <%-- Thẻ 3: NGƯỜI THUÊ --%>
                    <c:choose>
                        <c:when test="${isRoomEmpty}">
                            <div class="card info-card" style="text-align: center;">
                                <h3 class="text-muted">Phòng hiện đang trống</h3>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card info-card">
                                <h3 class="text-success" style="margin-top: 0; margin-bottom: 10px;">👥 Danh sách Người thuê</h3>
                                <c:forEach items="${listStudents}" var="st" varStatus="loop">
                                    <div style="${!loop.last ? 'border-bottom: 1px dashed #cbd5e1; margin-bottom: 10px; padding-bottom: 10px;' : ''}">
                                        <p style="margin: 0 0 5px 0;"><b>Họ Tên:</b> ${st.fullName}</p>
                                        <p style="margin: 0 0 5px 0;"><b>CCCD:</b> ${st.studentCode}</p>
                                        <p style="margin: 0;"><b>SĐT:</b> ${st.phone}</p>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${viewInvoiceDetail}">
                    <div class="table-wrap" style="margin-top: 24px; border: 2px solid #3b82f6; box-shadow: 0 4px 15px rgba(59, 130, 246, 0.1);">
                        <h3 style="color: #1e3a8a; padding: 15px 15px 0 15px; margin-top: 0;">🔍 Chi tiết phí Hóa đơn #${invoiceDetail.invoiceId}</h3>
                        <table>
                            <thead>
                                <tr>
                                    <th>STT</th><th>Nội dung</th><th>Số lượng</th><th>Đơn giá</th><th class="text-right">Thành tiền</th>
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
                                    <td class="text-danger font-bold text-right" style="font-size: 16px;"><fmt:formatNumber value="${invoiceDetail.totalAmount}" pattern="#,###"/>đ</td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </c:if>
            </div>
            
        </div>
    </body>
</html>