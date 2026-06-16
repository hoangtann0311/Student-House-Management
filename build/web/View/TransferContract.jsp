<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Chuyển nhượng Hợp đồng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />
        <div class="center-wrap">
            <div class="center-card">
                <h3 class="page-title" style="margin-bottom: 20px;">Chuyển nhượng người thuê</h3>
                <div class="alert-box alert-info" style="border-left: 4px solid #8b5cf6; flex-direction: column; align-items: flex-start;">
                    <p style="margin: 0; font-size: 14px;">Hợp đồng: <b>#${contractId}</b></p>
                    <p style="margin: 5px 0 0 0;">Người chuyển đi: <b class="text-danger">${oldName}</b></p>
                </div>

                <form action="TransferContract" method="POST">
                    <input type="hidden" name="contractId" value="${contractId}">
                    
                    <div class="form-group">
                        <label class="required">Chọn người muốn chuyển đi:</label>
                        <select name="oldUserId" required class="input text-danger">
                            <option value="">-- Chọn người sẽ rời phòng --</option>
                            <c:forEach items="${currentTenants}" var="ct">
                                <option value="${ct.userId}">${ct.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="required">Chọn sinh viên thay thế (Người mới):</label>
                        <select name="newUserId" required class="input">
                            <option value="">-- Chọn sinh viên (Họ tên) --</option>
                            <c:forEach items="${listStudent}" var="s">
                                <option value="${s.userId}">${s.fullName} - CCCD: ${s.studentCode}</option>                            
                            </c:forEach>
                        </select>
                    </div>

                    <div class="actions">
                        <button type="submit" class="btn-main" style="flex: 2; background: #8b5cf6;">Xác nhận thay thế</button>
                        <a href="Contract" class="btn-secondary" style="flex: 1; text-align: center;">Hủy</a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>