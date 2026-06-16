<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Xác nhận thu tiền</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="center-wrap">
            <div class="center-card">
                <h3 class="page-title" style="margin-bottom: 15px;">Xác nhận thu tiền #${invoice.invoiceId}</h3>
                <p>Phòng: <b>${invoice.roomNumber}</b></p>
                <p>Số tiền: <b class="text-danger"><fmt:formatNumber value="${invoice.totalAmount}" pattern="#,###"/>đ</b></p>

                <form action="PayInvoice" method="POST">
                    <input type="hidden" name="invoiceId" value="${invoice.invoiceId}">
                    <p class="font-bold">Chọn phương thức thanh toán:</p>
                    <div class="payment-methods">
                        <label class="method-option">
                            <input type="radio" name="paymentMethod" value="Tiền mặt" checked id="methodCash">
                            <span class="method-label-text">Tiền mặt 💵</span>
                        </label>

                        <label for="methodTransfer" class="method-option">
                            <input type="radio" name="paymentMethod" value="Chuyển khoản" id="methodTransfer">
                            <span class="method-label-text">Chuyển khoản 💳</span>
                        </label>

                        <div class="qr-box">
                            <p class="text-muted font-bold">Quét mã VietQR để thanh toán:</p>
                            <c:set var="bankID" value="MB" />
                            <c:set var="accountNo" value="0396219429" />
                            <c:set var="accountName" value="HOANG VAN TAN" />
                            <c:set var="description" value="Thanh toan phong ${invoice.roomNumber} thang ${invoice.invoiceMonth}" />
                            
                            <%-- ÉP KIỂU SỐ TIỀN VỀ SỐ NGUYÊN TRƯỚC KHI TRUYỀN VÀO URL --%>
                            <fmt:formatNumber var="amountStr" value="${invoice.totalAmount}" pattern="0" />

                            <img src="https://img.vietqr.io/image/${bankID}-${accountNo}-compact2.png?amount=${amountStr}&addInfo=${description}&accountName=${accountName}" 
                                 alt="QR Thanh toán" class="qr-img">

                            <div style="margin-top: 15px; font-size: 14px; line-height: 1.6;">
                                <p>Số tiền: <b class="text-danger"><fmt:formatNumber value="${invoice.totalAmount}" pattern="#,###"/>đ</b></p>
                                <p>Nội dung: <b>${description}</b></p>
                            </div>
                        </div>
                    </div>

                    <div class="actions" style="margin-top: 25px;">
                        <a href="Invoice" class="btn-secondary" style="flex: 1; text-align: center;">Hủy</a>
                        <button type="submit" class="btn-main" style="flex: 2; background: var(--success);">Xác nhận thu tiền</button>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>