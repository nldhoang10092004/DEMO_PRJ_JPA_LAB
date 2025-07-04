<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Employee Form</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
            }
            .form-group {
                margin-bottom: 15px;
            }
            label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }
            input[type="text"],
            input[type="email"] {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box;
            }
            .btn {
                display: inline-block;
                padding: 6px 12px;
                margin-bottom: 0;
                font-size: 14px;
                font-weight: 400;
                line-height: 1.42857143;
                text-align: center;
                white-space: nowrap;
                vertical-align: middle;
                cursor: pointer;
                border: 1px solid transparent;
                border-radius: 4px;
                text-decoration: none;
            }
            .btn-primary {
                color: #fff;
                background-color: #337ab7;
                border-color: #2e6da4;
            }
            .btn-default {
                color: #333;
                background-color: #fff;
                border-color: #ccc;
            }
        </style>
    </head>
    <body>
        <h1>Employee Form</h1>
        <form action="${pageContext.request.contextPath}/users?action=update" method="post">

            <div class="form-group">
                <label for="id">ID:</label>
                <input type="text" id="id" name="id" placeholder="ID" value = "${user.id}">
            </div>

            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" placeholder="User Name"  value = "${user.username}">
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" placeholder="Email"  value = "${user.email}">
            </div>

            <div class="form-group">
                <label for="country">Country</label>
                <input type="text" id="country" name="country" placeholder="Country"   value = "${user.country}">
            </div>

            <div class="form-group">
                <label for="role">Role</label>
                <input type="text" id="role" name="role" placeholder="Role"  value = "${user.role}">
            </div>   

            <div class="form-group">
                <label for="status">Status</label>
                <input type="checkbox" id="status" name="status" value="true"
                       <c:if test="${user.status}">checked</c:if> >

                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Password"  value = "${user.password}">
            </div>

            <div class="form-group">
                <label for="dob">Date of Birth</label>
                <input type="date" id="dob" name="dob">
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Save</button>
            </div>
        </form>
                       <c:if test="${not empty error}">
    <p class="message">${error}</p>
    <c:remove var="message" scope="session"/>
</c:if>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-default">Cancel</a>
    </body>
</html>