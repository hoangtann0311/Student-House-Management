<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="site-header">
    <div class="container site-header__inner">
        <a class="site-header__left" href="${pageContext.request.contextPath}/Home">
            <img class="site-logo" src="${pageContext.request.contextPath}/static/img/logo.jpg" alt="Logo" />
        </a>

        <div class="site-header__center" style="display: flex; gap: 35px; justify-content: center; align-items: center;">

            <a class="btn-nav" href="${pageContext.request.contextPath}/Home" style="text-decoration: none; color: white; font-weight: bold; border: none; background: transparent;">
                TRANG CHỦ
            </a>

            <span style="font-size: 18px; font-weight: 900; color: white; letter-spacing: 0.5px; line-height: 1;">
                Quản lý phòng trọ
            </span>

            <c:if test="${sessionScope.user.roleId == 1}">
                
                <a class="btn-nav" href="${pageContext.request.contextPath}/Dashboard" style="text-decoration: none; color: white; font-weight: bold; border: none; background: transparent; transition: color 0.3s ease;">
                    THỐNG KÊ
                </a>

                <div class="dropdown">
                    <a href="#" class="btn-nav" style="text-decoration: none; color: white; font-weight: bold; border: 2px solid rgba(255, 255, 255, 0.6); border-radius: 30px; padding: 8px 20px; display: inline-flex; align-items: center; gap: 8px; background: rgba(255,255,255,0.15); transition: all 0.3s ease;">
                        PHÒNG TRỌ ▼
                    </a>

                    <div class="dropdown-content">
                        <div class="dropdown-horizontal">
                            <a href="${pageContext.request.contextPath}/Room?status=Trống" class="tile">
                                <h3 style="margin: 0; font-size: 16px; color: #1b66d1;">Phòng Trống</h3>
                                <p style="margin: 5px 0 0; font-size: 13px;">Phòng chưa có người</p>
                            </a>

                            <a href="${pageContext.request.contextPath}/Room?status=Đang+thuê" class="tile">
                                <h3 style="margin: 0; font-size: 16px; color: #1b66d1;">Phòng Đã Thuê</h3>
                                <p style="margin: 5px 0 0; font-size: 13px;">Phòng đang có người ở</p>
                            </a>
                        </div>
                    </div>
                </div>
                
            </c:if>
        </div>

        <div class="site-header__right" style="display: flex; justify-content: flex-end; align-items: center; gap: 15px;">

            <div class="dropdown" style="display: flex; align-items: center;">
                <div style="cursor: pointer; display: flex; align-items: center;">
                    <span style="color: #e2e8f0; font-size: 14.5px; display: flex; align-items: center; line-height: 1; margin: 0;">
                        Xin chào, &nbsp;<b style="color: white; font-size: 15px;"><c:out value="${sessionScope.user.username}"/></b>&nbsp; 
                        <span style="font-size: 11px; margin-left: 3px;">▼</span>
                    </span>
                </div>

                <div class="dropdown-content" style="left: auto; right: 0; transform: none; padding-top: 18px; margin-top: 0;">
                    <div class="dropdown-vertical">
                        <a href="${pageContext.request.contextPath}/Profile" class="menu-item">
                            Thông tin cá nhân
                        </a>
                        <a href="${pageContext.request.contextPath}/ChangePassword" class="menu-item">
                            Đổi mật khẩu
                        </a>
                        <div style="height: 1px; background: #e2e8f0; margin: 4px 0;"></div>
                        <a href="${pageContext.request.contextPath}/Logout" class="menu-item logout">
                            Đăng xuất
                        </a>
                    </div>
                </div>
            </div>

            <span class="role-badge">
                <c:choose>
                    <c:when test="${sessionScope.user.roleId == 1}">ADMIN</c:when>
                    <c:otherwise>STUDENT</c:otherwise>
                </c:choose>
            </span>

        </div>
    </div>
</header>