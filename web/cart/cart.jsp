<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Giỏ hàng</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f9f9f9;
                padding: 20px;
                text-align: center;
            }

            table {
                width: 80%;
                margin: auto;
                border-collapse: collapse;
                background-color: #fff;
                box-shadow: 0 4px 8px rgba(0,0,0,0.05);
                border-radius: 8px;
                overflow: hidden;
            }

            th, td {
                padding: 12px;
                border-bottom: 1px solid #eee;
            }

            th {
                background-color: #20c997;
                color: white;
                text-transform: uppercase;
            }

            td form {
                display: inline-block;
                margin: 0 2px;
            }

            input[type=submit] {
                padding: 6px 12px;
                background-color: #20c997;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                transition: background-color 0.3s ease;
            }

            input[type=submit]:hover {
                background-color: #17a589;
            }

            input[type=submit]:disabled {
                background-color: #ccc;
                cursor: not-allowed;
            }

            a {
                color: #20a745;
                text-decoration: none;
                font-weight: bold;
                margin: 10px;
                display: inline-block;
            }

            a:hover {
                text-decoration: underline;
            }

            .message {
                margin-top: 20px;
                font-weight: bold;
                color: green;
            }

            h2 {
                color: #333;
                margin-bottom: 20px;
            }
        </style>
    </head>

    <body>
        <a href="${pageContext.request.contextPath}/productListCart.jsp">← Tiếp tục mua sắm</a>
        |
        <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>

        <p>${user.email}</p>
        <h2>🛒 Giỏ hàng của bạn</h2>

        <table>
            <thead>
                <tr>
                    <th>Sản phẩm</th>
                    <th>Giá</th>
                    <th>Số lượng</th>
                    <th>Tổng</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${cart}">
                    <tr>
                        <td>${item.product.name}</td>
                        <td>${item.product.price} VNĐ</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="action" value="update"/>
                                <input type="hidden" name="productId" value="${item.product.id}"/>
                                <input type="hidden" name="quantity" value="${item.quantity - 1}"/>
                                <input type="submit" value="-" ${item.quantity == 1 ? "disabled" : ""}/>
                            </form>
                            <strong>${item.quantity}</strong>
                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="action" value="update"/>
                                <input type="hidden" name="productId" value="${item.product.id}"/>
                                <input type="hidden" name="quantity" value="${item.quantity + 1}"/>
                                <input type="submit" value="+"/>
                            </form>
                        </td>
                        <td>${item.product.price * item.quantity} VNĐ</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="action" value="remove"/>
                                <input type="hidden" name="productId" value="${item.product.id}"/>
                                <input type="submit" value="Xoá"/>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <br>
        <form action="${pageContext.request.contextPath}/cart" method="post">
            <input type="hidden" name="action" value="checkout"/>
            <input type="submit" value="✅ Tiến hành thanh toán"/>
        </form>

        <c:if test="${not empty message}">
    <p class="message">${message}</p>
    <c:remove var="message" scope="session"/>
</c:if>

    </body>
</html>
