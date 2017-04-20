package com.dlc.buscador.modelo.vectorial.entidades;

import java.io.*;

/**
 *
 * @author nicolashefty
 */
public class Vocabulario implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String vocabulario;

    private int nr; // Cantidad de documentos en los que aparece el término

    private int N; // Cantidad de documentos base

    private int max_tf; // Frecuencia de aparicion maxima en algun documento

    public Vocabulario(String vocabulario)
    {
        this.vocabulario = vocabulario;
        this.nr = 1;
    }

    public Vocabulario(String vocabulario, int nr, int N, int max_tf)
    {
        this.vocabulario = vocabulario;
        this.nr = nr;
        this.N = N;
        this.max_tf = max_tf;
    }

    public String getVocabulario()
    {
        return vocabulario;
    }

    public void setVocabulario(String vocabulario)
    {
        this.vocabulario = vocabulario;
    }

    public int getNr()
    {
        return this.nr;
    }

    public void sumarDocumento()
    {
        this.nr++;
    }

    public int getMax_tf()
    {
        return max_tf;
    }

    public void setMax_tf(int max_tf)
    {
        this.max_tf = max_tf;
    }

    public int getN()
    {
        return N;
    }

    public void setN(int N)
    {
        this.N = N;
    }

    public double getIdf()
    {
        return Math.log10((double) N / (double) nr);
    }

    @Override
    public String toString()
    {
        return "Vocabulario{" + "vocabulario=" + vocabulario + ", posteos=" + nr + '}';
    }

}
