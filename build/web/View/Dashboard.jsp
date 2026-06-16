<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thống Kê Hệ Thống</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">

        <style>
            /* Tinh chỉnh để Bootstrap không phá form cũ */
            body {
                background-color: #f4f6f9;
            }
            a {
                text-decoration: none;
            } /* Tắt gạch chân mặc định của Bootstrap */

            /* Đẩy cái bảng thống kê xuống một chút cho khỏi dính vào Header */
            .dashboard-container {
                margin-top: 40px;
                padding-bottom: 50px;
            }
        </style>
    </head>
    <body>
        <jsp:include page="common/header-auth.jsp" />

        <div class="container dashboard-container">
            <div class="d-flex justify-content-between align-items-center mb-4 p-3 shadow" 
                 style="background-color: rgba(255, 255, 255, 0.95); border-radius: 12px; border: 1px solid rgba(255,255,255,0.5);">

                <h2 class="fw-bold text-primary m-0" style="font-size: 1.8rem; text-shadow: 1px 1px 2px rgba(0,0,0,0.1);">
                    <i class="fa-solid fa-chart-line me-2"></i>Thống Kê Doanh Thu & Phòng
                </h2>

                <a href="Home" class="btn btn-primary fw-bold shadow-sm px-4 py-2" style="border-radius: 8px;">
                    <i class="fa-solid fa-arrow-left me-2"></i>Trở về Trang chủ
                </a>

            </div>

            <div class="row g-4">
                <div class="col-md-3">
                    <div class="card border-0 shadow-sm bg-success text-white">
                        <div class="card-body text-center p-4">
                            <i class="fa-solid fa-sack-dollar fa-3x mb-3 opacity-75"></i>
                            <h5>Doanh Thu Tháng Này</h5>
                            <h3 class="fw-bold"><fmt:formatNumber value="${currentRevenue}" pattern="#,###"/> đ</h3>
                        </div>



                    </div>
                    <div class="card border-0 shadow-sm bg-success text-white">
                        <div class="card-body text-center p-4">
                            <i class="fa-solid fa-sack-dollar fa-3x mb-3 opacity-75"></i>
                            <h5>Doanh Thu Tháng Trước nữa</h5>
                            <h3 class="fw-bold"><fmt:formatNumber value="" pattern="#,###"/> đ</h3>
                        </div>
                    </div>
                    <div class="card border-0 shadow-sm bg-success text-white">
                        <div class="card-body text-center p-4">
                            <i class="fa-solid fa-sack-dollar fa-3x mb-3 opacity-75"></i>
                            <h5>Doanh Thu Tháng Trước</h5>
                            <h3 class="fw-bold"><fmt:formatNumber value="" pattern="#,###"/> đ</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card border-0 shadow-sm bg-danger text-white">
                        <div class="card-body text-center p-4">
                            <i class="fa-solid fa-file-invoice fa-3x mb-3 opacity-75"></i>
                            <h5>Hóa Đơn Chờ Thu</h5>
                            <h3 class="fw-bold">${unpaidInvoices} Hóa đơn</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card border-0 shadow-sm bg-primary text-white">
                        <div class="card-body text-center p-4">
                            <i class="fa-solid fa-users fa-3x mb-3 opacity-75"></i>
                            <h5>Sinh Viên Đang Ở</h5>
                            <h3 class="fw-bold">${totalStudents} Người</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card border-0 shadow-sm bg-info text-dark">
                        <div class="card-body text-center p-4">
                            <i class="fa-solid fa-door-open fa-3x mb-3 opacity-75"></i>
                            <h5>Phòng Đang Trống</h5>
                            <h3 class="fw-bold">${availableRooms} / ${totalRooms} Phòng</h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>