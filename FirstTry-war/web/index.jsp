<%-- 
    Document   : index
    Created on : 19-abr-2017, 21:40:48
    Author     : nicolashefty
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <title>Buscador de palabras</title>


        <link rel="stylesheet" href="css/bootstrap.min.css">
        <link rel="stylesheet" href="css/bootflat.min.css">
        <link rel="stylesheet" href="css/estilos.css">

    </head>
    <body>

        <div class="container">
            <h1>Buscador de palabras</h1>
            <form action="buscarFrase" method="get">
                <div class="input-group">
                    <input type="text" placeholder="Busca una frase" class="form-control" name="frase">
                    <span class="input-group-btn">
                        <button class="btn btn-primary">Buscar</button>
                    </span>
                </div>
            </form>


        <script src="js/jquery.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
    </body>
</html>



