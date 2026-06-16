<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Trang chủ - Quản lý phòng trọ</title>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/components.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
    </head>
    <body class="page">
        <jsp:include page="common/header-auth.jsp" />

        <main class="container main">
            <div class="card">
                <div class="grid">
                    <c:choose>
                        <%-- CHỨC NĂNG DÀNH CHO ADMIN --%>
                        <c:when test="${sessionScope.user.roleId == 1}">
                            <a class="tile" href="${pageContext.request.contextPath}/User">
                                <div>
                                    <h3>Quản lý tài khoản</h3>
                                    <p>Thêm / sửa / xoá tài khoản, phân quyền</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/Room">
                                <div>
                                    <h3>Quản lý phòng</h3>
                                    <p>Danh sách phòng, tình trạng, giá thuê</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/Contract">
                                <div>
                                    <h3>Quản lý hợp đồng</h3>
                                    <p>Danh sách hợp đồng, thời hạn, tiền cọc</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/MeterReading">
                                <div>
                                    <h3>Quản lý điện nước</h3>
                                    <p>Ghi và xem chỉ số điện, nước hàng tháng</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/Invoice">
                                <div>
                                    <h3>Quản lý hóa đơn</h3>
                                    <p>Danh sách hóa đơn, tình trạng thanh toán</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/PaymentHistory">
                                <div>
                                    <h3>Lịch sử thanh toán</h3>
                                    <p>Xem chi tiết ngày giờ và dòng tiền đã thu</p>
                                </div>
                            </a>
                            
                            <%-- ĐÂY LÀ NÚT MỚI ĐƯỢC THÊM VÀO ĐỂ GỌI BOOTSTRAP --%>
                            <a class="tile" href="${pageContext.request.contextPath}/Dashboard">
                                <div>
                                    <h3 style="color: black;">Thống kê</h3>
                                    <p>Xem báo cáo tổng quan</p>
                                </div>
                            </a>
                            
                        </c:when>

                        <%-- CHỨC NĂNG DÀNH CHO SINH VIÊN (STUDENT) --%>
                        <c:otherwise>
                            <a class="tile" href="${pageContext.request.contextPath}/MyRoom">
                                <div>
                                    <h3>Phòng của tôi</h3>
                                    <p>Xem thông tin phòng đang thuê</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/MyContract">
                                <div>
                                    <h3>Hợp đồng của tôi</h3>
                                    <p>Xem hợp đồng của phòng đang thuê</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/Invoice">
                                <div>
                                    <h3>Hóa đơn của tôi</h3>
                                    <p>Xem các hóa đơn của phòng đang thuê</p>
                                </div>
                            </a>
                            <a class="tile" href="${pageContext.request.contextPath}/MyPaymentHistory">
                                <div>
                                    <h3>Lịch sử thanh toán</h3>
                                    <p>Xem danh sách các khoản tiền đã đóng</p>
                                </div>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </main>

        <footer class="footer">
            <div class="container footer-inner">
                <div class="footer-col">
                    <div class="footer-title">Quản lý nhà trọ sinh viên</div>
                    <div class="footer-text">Hệ thống quản lý phòng, hợp đồng và người thuê.</div>
                </div>
                <div class="footer-col">
                    <div class="footer-title">Liên hệ</div>
                    <div class="footer-text">Địa chỉ: Thạch Thất, Hà Nội</div>
                    <div class="footer-text">Hotline: 0933 260 839</div>
                    <div class="footer-text">Email: support@nhatro.com</div>
                </div>
                <div class="footer-col">
                    <div class="footer-title">Hỗ trợ</div>
                    <div class="footer-text">Giờ làm việc: 08:00 - 17:30</div>
                    <div class="footer-text">Hướng dẫn sử dụng</div>
                    <div class="footer-text">Chính sách & bảo mật</div>
                </div>
            </div>
            <div class="footer-bottom">&copy; 2026 - Hệ thống quản lý phòng trọ</div>
        </footer>
    </body>
</html>