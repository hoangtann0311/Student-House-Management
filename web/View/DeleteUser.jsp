<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Delete User</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <div class="center-wrap">
            <div class="center-card">
                <h2 class="text-danger">⚠️ Xác nhận xóa tài khoản</h2>
                <p>Bạn có chắc chắn muốn xóa tài khoản có ID: <b>${id}</b> không?</p>
                <p class="text-muted"><i>Lưu ý: Hành động này không thể hoàn tác và hệ thống sẽ chặn nếu người này đang thuê phòng.</i></p>

                <form action="DeleteUser" method="POST">
                    <input type="hidden" name="id" value="${id}">
                    <div class="form-actions" style="justify-content: center;">
                        <button type="submit" class="btn btn-danger">XÁC NHẬN XÓA</button>
                        <a href="User" class="btn btn-secondary">HỦY BỎ</a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>