package com.dlc.buscador.modelo.vectorial.entidades;

import java.io.*;

/**
 *
 * @author nicolashefty
 */
public class Posteo implements Serializable
{

    private String documento;

    private int tf; // Frecuencia de aparicion en el documento

    private String contexto;

    public Posteo(String documento)
    {
        this.documento = documento;
        this.tf = 1;
        this.contexto = "";
    }

    public Posteo(String documento, int tf)
    {
        this.documento = documento;
        this.tf = tf;
    }

    public Posteo(String documento, int tf, String contexto)
    {
        this.documento = documento;
        this.tf = tf;
        this.contexto = contexto;
    }

    public String getDocumento()
    {
        return documento;
    }

    public void setDocumento(String documento)
    {
        this.documento = documento;
    }

    public int getTf()
    {
        return tf;
    }

    public void setTf(int tf)
    {
        this.tf = tf;
    }

    public void sumarTf()
    {
        this.tf++;
    }

    public String getContexto()
    {
        return contexto;
    }

    public void setContexto(String contexto)
    {
        if (this.contexto == "")
        {
            contexto = this.contexto + " ... " + contexto;
        }

        this.contexto = contexto;
    }

}
