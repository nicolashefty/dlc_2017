package com.dlc.buscador.modelo.vectorial.proceso;

import com.dlc.buscador.modelo.vectorial.entidades.*;
import com.dlc.buscador.modelo.vectorial.entidades.db.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author nicolashefty
 */
public class Procesar
{

    private static HashMap<String, Vocabulario> vocabularios;

    private static HashMap<String, ArrayList<Posteo>> posteos;

    private static HashMap<String, String> documentos;

    private static HashMap<String, String> palabras_afectadas;

    public Procesar()
    {
        Procesar.vocabularios = Storage.materializarVocabulario();
        Procesar.posteos = Storage.materializarPosteo();
        Procesar.documentos = Storage.materializarDocumentos();
        Procesar.palabras_afectadas = new HashMap<>();
    }

    public void finalizar() throws IOException
    {
        Storage.desmaterializarVocabulario(vocabularios);
        Storage.desmaterializarPosteo(posteos);
        Storage.desmaterializarDocumentos(documentos);
    }

    public void actualizar()
    {
        int N = documentos.size();
        for (Map.Entry<String, String> entry : palabras_afectadas.entrySet())
        {
            String palabra = entry.getKey();
            Vocabulario vocabulario = vocabularios.get(palabra);
            ArrayList<Posteo> posteos_voc = posteos.get(palabra);
            int max_tf = vocabulario.getMax_tf();

            for (Posteo posteo : posteos_voc)
            {
                if (posteo.getTf() > max_tf)
                {
                    max_tf = posteo.getTf();
                }
            }

            vocabulario.setMax_tf(max_tf);

            Collections.sort(posteos_voc, new Comparator<Posteo>()
                     {
                         @Override
                         public int compare(Posteo p1, Posteo p2)
                         {
                             return p2.getTf() - p1.getTf();
                         }

                     });
        }

        for (Map.Entry<String, Vocabulario> entry : vocabularios.entrySet())
        {
            Vocabulario vocabulario = entry.getValue();

            vocabulario.setN(N);
        }

        palabras_afectadas = null;
    }

    public void ejecutar(File archivo)
    {
        HashMap<String, Posteo> palabras_documento = new HashMap<>();
        String texto = Leer.getTexto(archivo);
        String documento = archivo.getName();

        if (texto == null || texto == "")
        {
            return;
        }

        if (documentos.get(documento) == null)
        {
            documentos.put(documento, null);
        }
        else
        {
            return;
        }

        String limpio = Leer.limpiar(texto);
        String[] palabras = limpio.split("\\s");

        int c = palabras.length;
        for (int i = 0; i < c; i++)
        {
            String palabra = palabras[i];

            if (palabra.trim() == "")
            {
                continue;
            }

            if (palabras_afectadas.get(palabra) == null)
            {
                palabras_afectadas.put(palabra, null);
            }

            Vocabulario vocabulario = vocabularios.get(palabra);
            ArrayList<Posteo> posteos_voc = posteos.get(palabra);
            Posteo posteo_doc = palabras_documento.get(palabra);

            if (vocabulario == null)
            {
                Vocabulario vocabulario_nuevo = new Vocabulario(palabra);
                Posteo posteo = new Posteo(documento);
                ArrayList<Posteo> listado_posteos = new ArrayList<>();
                String contexto = getContexto(i, c, palabras, 10);

                posteo.setContexto(contexto);
                listado_posteos.add(posteo);

                posteos.put(palabra, listado_posteos);
                vocabularios.put(palabra, vocabulario_nuevo);

                palabras_documento.put(palabra, posteo);
            }
            else if (posteo_doc == null)
            {
                String contexto = getContexto(i, c, palabras, 5);
                Posteo posteo = new Posteo(documento);
                posteo.setContexto(contexto);

                vocabulario.sumarDocumento();
                posteos_voc.add(posteo);
                palabras_documento.put(palabra, posteo);
            }
            else
            {
                String contexto = getContexto(i, c, palabras, 5);
                posteo_doc.sumarTf();
                posteo_doc.setContexto(contexto);
            }
        }

    }

    private String getContexto(int i, int c, String[] palabras, int contexto)
    {
        StringBuilder buffer = new StringBuilder();

        int before = Math.max(0, i - contexto);
        int after = Math.min(c - 1, i + contexto);

        for (int j = before; j <= after; j++)
        {
            buffer.append(palabras[j]).append(" ");
        }

        return buffer.toString().trim();
    }

}
