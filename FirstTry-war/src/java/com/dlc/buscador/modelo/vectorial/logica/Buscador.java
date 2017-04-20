package com.dlc.buscador.modelo.vectorial.logica;

import com.dlc.buscador.modelo.vectorial.entidades.*;
import com.dlc.buscador.modelo.vectorial.entidades.db.*;
import java.util.*;

/**
 *
 * @author nicolashefty
 */
public class Buscador
{

    public static ArrayList<Documento> buscar(String q)
    {
        HashMap<String, Vocabulario> hm_vocabulario = Storage.materializarVocabulario();
        ArrayList<Vocabulario> vocabularios = new ArrayList<>();
        HashMap<String, Documento> R = new HashMap<>();

        String[] palabras = q.split("\\s");
        for (String palabra : palabras)
        {
            Vocabulario voc = hm_vocabulario.get(palabra);
            if (voc == null || vocabularios.contains(voc))
            {
                continue;
            }

            vocabularios.add(voc);
        }

        Collections.sort(vocabularios, new Comparator<Vocabulario>()
                 {
                     @Override
                     public int compare(Vocabulario p1, Vocabulario p2)
                     {
                         return (int) (p2.getIdf() - p1.getIdf());
                     }

                 });

        for (Vocabulario vocabulario : vocabularios)
        {
            ArrayList<Posteo> posteos = Storage.materializarPosteo(vocabulario.getVocabulario());
            int aux = Math.min(10, posteos.size());
            for (Posteo posteo : posteos)
            {
                if (aux-- == 0)
                {
                    break;
                }

                Documento doc = R.get(posteo.getDocumento());
                if (doc == null)
                {
                    doc = new Documento(posteo.getDocumento());
                    R.put(posteo.getDocumento(), doc);
                }

                doc.sumarPeso(posteo, vocabulario);
                doc.putContexto(posteo.getContexto());
            }
        }

        ArrayList<Documento> documentos = new ArrayList<Documento>(R.values());

        Collections.sort(documentos, new Comparator<Documento>()
                 {
                     @Override
                     public int compare(Documento d1, Documento d2)
                     {
                         return (int) (d2.getPeso() - d1.getPeso()) * 100;
                     }

                 });

        return documentos;
    }

}
