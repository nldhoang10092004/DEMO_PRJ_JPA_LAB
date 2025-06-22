<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="productDAO.ProductDAO" %>
<%@ page import="java.util.List" %>
<jsp:useBean id="ProductDAO" class="productDAO.ProductDAO" scope="request"/>
<jsp:useBean id="products" class="java.util.ArrayList" scope="request"/>
<%
    request.setAttribute("products", ProductDAO.selectAllProducts());
%>

<html>
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Sản Phẩm</title>

        <style>
            body {
                font-family: Arial, sans-serif;
                text-align: center;
            }
            .product {
                border: 1px solid #ddd;
                padding: 10px;
                margin: 10px;
                display: inline-block;
                width: 250px;
            }
            button, input[type=submit] {
                background: #20a745;
                color: white;
                padding: 5px;
                border: none;
                cursor: pointer;
            }
        </style>
    </head>
    <body>
        <h2>Danh Sách Sản Phẩm</h2>
        <a href="<%= request.getContextPath()%>/cart/cart.jsp">Xem Giỏ Hàng</a><br><br>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>

        <c:forEach var="product" items="${products}">
            <div class="product">
                <h3>${product.name}</h3>
                <p>${product.stock} Sản Phẩm</p>
                <p>${product.price} VNĐ</p>
                <form action="<%= request.getContextPath()%>/cart" method="post">
                    <input type="hidden" name="action" value="add"/>
                    <input type="hidden" name="productId" value="${product.id}"/>
                    <c:choose>
                        <c:when test="${product.stock > 0}">
                            <input type="number" name="quantity" value="1" min="1" max="${product.stock}" />
                            <input type="submit" value="Thêm vào giỏ"/>
                        </c:when>
                        <c:otherwise>
                            <p color = "Red">Hết hàng</p>
                        </c:otherwise>
                    </c:choose>
                </form>
            </div>
        </c:forEach>
    </body>
</html>
