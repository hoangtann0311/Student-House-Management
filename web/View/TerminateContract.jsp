<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Xác nhận thanh lý hợp đồng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="center-wrap">
            <div class="center-card" style="border-top: 5px solid var(--danger);">
                <h2 class="page-title text-danger">Xác nhận Kết thúc Hợp đồng</h2>
                <div style="margin: 20px 0; line-height: 1.6;">
                    <p>Hợp đồng: <b>#${contract.contractID}</b> - Phòng: <b>${contract.roomNumber}</b></p>
                    <p>Ngày kết thúc dự kiến: <b>${contract.endDate}</b></p>
                </div>

                <c:choose>
                    <c:when test="${isEarly}">
                        <div class="alert-box alert-danger" style="flex-direction: column; align-items: flex-start;">
                            <h4 class="text-danger" style="margin-top: 0;">CẢNH BÁO: TRẢ PHÒNG SỚM</h4>
                            <p class="font-bold">Khách hàng đang thực hiện trả phòng trước thời hạn!</p>
                            <p>Hành động này đồng nghĩa với việc <b>Phá hợp đồng</b>. Số tiền cọc: <b style="font-size: 16px;"><fmt:formatNumber value="${contract.depositAmount}" pattern="#,###"/>đ</b> sẽ <b>KHÔNG</b> được hoàn trả.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert-box success-alert" style="flex-direction: column; align-items: flex-start;">
                            <h4 class="text-success" style="margin-top: 0;">Trả phòng đúng hạn</h4>
                            <p>Hợp đồng đã đến hạn kết thúc. Vui lòng kiểm tra phòng và hoàn trả tiền cọc: <b><fmt:formatNumber value="${contract.depositAmount}" pattern="#,###"/>đ</b> cho khách.</p>
                        </div>
                    </c:otherwise>
                </c:choose>

                <form action="TerminateContract" method="POST">
                    <input type="hidden" name="contractId" value="${contract.contractID}">
                    <input type="hidden" name="roomId" value="${roomId}">
                    <input type="hidden" name="isEarly" value="${isEarly}">
                    <div class="actions">
                        <button type="submit" class="btn-danger" style="flex: 2;">Xác nhận ${isEarly ? 'PHÁ HĐ & THU HỒI CỌC' : 'KẾT THÚC & TRẢ CỌC'}</button>
                        <a href="Contract" class="btn-secondary" style="flex: 1; text-align: center;">Hủy</a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>