package com.dlc.buscador.modelo.vectorial.proceso;

import java.io.*;
import java.util.*;

/**
 *
 * @author nicolashefty
 */
public class Leer
{

    public static String limpiar(String texto)
    {
        return texto.replaceAll("([a-zA-ZñáéíóúÑÁÉÍÓÚ]*)?[0-9]+([a-zA-ZñáéíóúÑÁÉÍÓÚ]*)?", "")
                .replaceAll("([^a-zA-ZñáéíóúÑÁÉÍÓÚ\\s]+)", " ")
                .replaceAll("(\\s+)", " ");
    }

    public static String getTexto(File archivo)
    {
        StringBuilder buffer = new StringBuilder();

        try (Scanner sc = new Scanner(archivo, "iso-8859-1"))
        {
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                buffer.append(line).append(" ");
            }
        }
        catch (Exception e)
        {
            return null;
        }

        return buffer.toString().trim();
    }

}
