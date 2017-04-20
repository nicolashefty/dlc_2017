package com.dlc.buscador.modelo.vectorial.desk.ventana;

import com.dlc.buscador.modelo.vectorial.entidades.*;
import com.dlc.buscador.modelo.vectorial.logica.*;
import java.util.*;

/**
 *
 * @author nicolashefty
 */
public class Main
{

    public static void main(String[] args)
    {

        System.out.println("Buscando: E io, che del color mi fui accorto");
        System.out.println("-----------------");
        ArrayList<Documento> R = Buscador.buscar("E io, che del color mi fui accorto");

        System.out.println("Cantidad de resultados: " + R.size());
        System.out.println("-----------------");

        for (Documento documento : R)
        {
            System.out.println(documento);
        }

    }

}
