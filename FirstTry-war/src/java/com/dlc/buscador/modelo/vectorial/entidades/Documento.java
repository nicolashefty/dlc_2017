package com.dlc.buscador.modelo.vectorial.entidades;

/**
 *
 * @author nicolashefty
 */
public class Documento
{

    private String nombre;

    private double peso;

    private String contexto;

    public Documento(String nombre)
    {
        this.nombre = nombre;
        this.peso = 0;
        this.contexto = "";
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public double getPeso()
    {
        return peso;
    }

    public void setPeso(double peso)
    {
        this.peso = peso;
    }

    public void sumarPeso(Posteo posteo, Vocabulario vocabulario)
    {
        peso += vocabulario.getIdf() * posteo.getTf();
    }

    public String getContexto()
    {
        return contexto;
    }

    public void setContexto(String contexto)
    {
        this.contexto = contexto;
    }

    public void putContexto(String contexto)
    {
        if (this.contexto != "")
        {
            contexto = this.contexto + " ... " + contexto;
        }

        this.contexto = contexto;
    }

    @Override
    public String toString()
    {
        return "Documento{" + "nombre=" + nombre + ", peso=" + peso + '}';
    }

}
