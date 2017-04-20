package com.dlc.buscador.modelo.vectorial.web.servlet.buscador;

import com.dlc.buscador.modelo.vectorial.entidades.*;
import com.dlc.buscador.modelo.vectorial.logica.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author nicolashefty
 */
public class buscarFrase extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String frase = request.getParameter("frase").trim();
        ArrayList<Documento> documentos = new ArrayList<>();
        double tiempo;
        int cantidad;

        long t1 = System.currentTimeMillis();
        if (frase != "") {
            documentos = Buscador.buscar(frase);

            String palabras = frase.replaceAll("\\s", "|");
            for (Documento documento : documentos) {
                String contexto = documento.getContexto().replaceAll("(" + palabras + ")", "<strong>$1</strong>");
                
                documento.setContexto(contexto);
            }

        }
        long t2 = System.currentTimeMillis();

        cantidad = documentos.size();
        tiempo = (t2 - t1) / 1000;

        request.setAttribute("frase", frase);
        request.setAttribute("documentos", documentos);
        request.setAttribute("tiempo", tiempo);
        request.setAttribute("cantidad", cantidad);

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/buscar.jsp");
        dispatcher.forward(request, response);

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
