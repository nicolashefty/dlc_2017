package com.dlc.buscador.modelo.vectorial.entidades.db;

import com.dlc.buscador.modelo.vectorial.entidades.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.sql.*;

/**
 *
 * @author nicolashefty
 */
public class Storage
{

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";

    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/dbdlc";

    private static HashMap<String, Vocabulario> vocabularios_cache = new HashMap<>();

    private static HashMap<String, ArrayList<Posteo>> posteos_cache = new HashMap<>();

    private static HashMap<String, String> documentos_cache = new HashMap<>();

    private static Connection _connection;

    //////////////////////////////
    private static Connection getConnection()
    {
//         try
//        {
//            if (_connection != null && !_connection.isClosed())
//            {
//                return _connection;
//            }
//            else
//            {
//                _connection = null;
//            }
//
//            Database db = new Database();
//            DataSource dataSource = db.getConnection();
//            if (dataSource != null)
//            {
//                _connection = dataSource.getConnection();
//            }
//            return _connection;
//        }
//        catch (SQLException ex)
//        {
//            return null;
//        }
        try
        {
            if (_connection != null && !_connection.isClosed())
            {
                return _connection;
            }
            else
            {
                _connection = null;
            }

            Class.forName(DB_DRIVER);
            _connection = DriverManager.getConnection(DB_CONNECTION, "dlcuser", "dlcpwd");

            return _connection;
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            return null;
        }
    }

    //////////////////////////////
    
    public static Vocabulario materializarVocabulario(String vocabulario)
    {
        Connection con = getConnection();
        if (vocabularios_cache.size() > 0
            && vocabularios_cache.containsKey(vocabulario)
            && vocabularios_cache.get(vocabulario) != null)
        {
            return vocabularios_cache.get(vocabulario);
        }

        Vocabulario voc = null;

        if (con == null)
        {
            new NullPointerException("Connection to DB is NULL").printStackTrace();
            return voc;
        }

        try
        {
            PreparedStatement st = con.prepareStatement("SELECT * FROM vocabulario WHERE vocabulario=?");
            st.setString(1, vocabulario);
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                String vocabul = rs.getString("vocabulario");
                int nr = rs.getInt("nr");
                int n = rs.getInt("n");
                int tf = rs.getInt("tf");

                voc = new Vocabulario(vocabul, nr, n, tf);
                vocabularios_cache.put(vocabul, voc);
                return voc;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return voc;
    }
    
    
    public static HashMap<String, Vocabulario> materializarVocabulario()
    {
        Connection con = getConnection();
        HashMap<String, Vocabulario> vocabularios = new HashMap<>();

        if (vocabularios_cache.size() > 0)
        {
            return vocabularios_cache;
        }

        if (con == null)
        {
            return vocabularios;
        }

        try
        {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM vocabulario");

            while (rs.next())
            {
                String vocabulario = rs.getString("vocabulario");
                int nr = rs.getInt("nr");
                int n = rs.getInt("n");
                int tf = rs.getInt("tf");

                vocabularios.put(vocabulario, new Vocabulario(vocabulario, nr, n, tf));
            }

            vocabularios_cache = vocabularios;

            return vocabularios;
        }
        catch (Exception ex)
        {
            return vocabularios;
        }
    }

    public static void desmaterializarVocabulario(HashMap<String, Vocabulario> vocabularios) throws IOException
    {
        Connection con = getConnection();
        PreparedStatement st = null;

        if (con == null)
        {
            return;
        }

        vocabularios_cache = vocabularios;

        try
        {
            boolean error = false;

            con.setAutoCommit(false);

            Statement st_delete = con.createStatement();
//            st_delete.executeUpdate("DELETE FROM vocabulario");

            
            for (Vocabulario v : vocabularios.values())
            {
                if (error)
                {
                    break;
                }

                if (v.esNuevo())
                {
                    st = con.prepareStatement("INSERT INTO vocabulario (vocabulario,nr,n,tf) VALUES  (?,?,?,?)");

                    st.setString(1, v.getVocabulario());
                    st.setInt(2, v.getNr());
                    st.setInt(3, v.getN());
                    st.setInt(4, v.getMax_tf());
                }
                else if (v.estaModificado())
                {
                    st = con.prepareStatement("UPDATE vocabulario SET nr = ?, n = ?, tf = ? WHERE vocabulario = ?");
                    st.setInt(1, v.getNr());
                    st.setInt(2, v.getN());
                    st.setInt(3, v.getMax_tf());
                    st.setString(4, v.getVocabulario());
                
                }
                else 
                {
                    st = con.prepareStatement("INSERT INTO vocabulario (vocabulario,nr,n,tf) VALUES  (?,?,?,?)");

                    st.setString(1, v.getVocabulario());
                    st.setInt(2, v.getNr());
                    st.setInt(3, v.getN());
                    st.setInt(4, v.getMax_tf());
                }

                try
                {
                    int resultado = st.executeUpdate();
                    if (resultado == 0)
                    {
                        error = true;
                    }
                    else
                    {
                        v.setModificado(false);
                        v.setNuevo(false);
                    }
                }
                catch (Exception ex)
                {
//                    ex.printStackTrace();
                    error = true;
                }
            }

            if (!error)
            {
                con.commit();
            }
            else
            {
                con.rollback();
            }

            con.setAutoCommit(true);
        }
        catch (Exception ex)
        {

            try
            {
                if (!con.isClosed())
                {
                    con.rollback();
                }
            }
            catch (SQLException ex1)
            {
            }
        }
        finally
        {
            try
            {
                if (st != null)
                {
                    st.close();
                }
                con.close();
            }
            catch (Exception ex)
            {
            }
        }

    }

    public static HashMap<String, ArrayList<Posteo>> materializarPosteo()
    {
        Connection con = getConnection();
        HashMap<String, ArrayList<Posteo>> posteos = new HashMap<>();

        if (con == null)
        {
            return posteos;
        }

        try
        {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM posteo");

            while (rs.next())
            {
                String palabra = rs.getString("palabra");
                String documento = rs.getString("documento");
                String contexto = rs.getString("contexto");
                int tf = rs.getInt("tf");

                ArrayList<Posteo> listado = posteos.get(palabra);

                if (listado == null)
                {
                    listado = new ArrayList<>();
                }

                listado.add(new Posteo(documento, tf, contexto));

                posteos.put(palabra, listado);
            }

            posteos_cache = posteos;

            return posteos;
        }
        catch (Exception ex)
        {
            return posteos;
        }
    }

    public static ArrayList<Posteo> materializarPosteo(String key)
    {
        Connection con = getConnection();
        ArrayList<Posteo> posteos = new ArrayList<>();

        if (posteos_cache.get(key) != null)
        {
            return posteos_cache.get(key);
        }

        if (con == null)
        {
            return posteos;
        }

        try
        {
            PreparedStatement st = con.prepareStatement("SELECT * FROM posteo WHERE palabra=?");
            st.setString(1, key);
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                String documento = rs.getString("documento");
                String contexto = rs.getString("contexto");

                int tf = rs.getInt("tf");

                posteos.add(new Posteo(documento, tf, contexto));
            }

            posteos_cache.put(key, posteos);

            return posteos;
        }
        catch (Exception ex)
        {
            return posteos;
        }
    }

    public static void desmaterializarPosteo(HashMap<String, ArrayList<Posteo>> posteos) throws IOException
    {
        Connection con = getConnection();
        PreparedStatement st = null;

        if (con == null)
        {
            return;
        }

        posteos_cache = posteos;

        try
        {
            boolean error = false;

            con.setAutoCommit(false);

            Statement st_delete = con.createStatement();
//            st_delete.executeUpdate("DELETE FROM posteo");


            for (Map.Entry<String, ArrayList<Posteo>> entry : posteos.entrySet())
            {
                String palabra = entry.getKey();
                ArrayList<Posteo> value = entry.getValue();

                if (error)
                {
                    break;
                }

                for (Posteo posteo : value)
                {
                    if (error)
                    {
                        break;
                    }

                    if (posteo.esNuevo()){

                        st = con.prepareStatement("INSERT INTO posteo (documento,palabra,tf,contexto) VALUES  (?,?,?,?)");
                        st.setString(1, posteo.getDocumento());
                        st.setString(2, palabra);
                        st.setInt(3, posteo.getTf());
                        st.setString(4, posteo.getContexto());
                    }
                    else if (posteo.estaModificado()) {
                        st = con.prepareStatement("UPDATE posteo SET tf = ? WHERE documento = ? AND palabra = ?");
                        st.setInt(1, posteo.getTf());
                        st.setString(2, posteo.getDocumento());
                        st.setString(3, palabra);
                    }
                    else
                    {
                        st = con.prepareStatement("INSERT INTO posteo (documento,palabra,tf,contexto) VALUES  (?,?,?,?)");
                        st.setString(1, posteo.getDocumento());
                        st.setString(2, palabra);
                        st.setInt(3, posteo.getTf());
                        st.setString(4, posteo.getContexto());
                    }

                    try
                    {
                        int resultado = st.executeUpdate();
                        if (resultado == 0)
                        {
                            error = true;
                        }
                        else
                        {
                            posteo.setModificado(false);
                            posteo.setNuevo(false);
                        }
                    }
                    catch (Exception ex)
                    {
                        System.out.println("There was an error");
                        ex.printStackTrace();
                        error = true;
                    }
                }

            }

            if (!error)
            {
                con.commit();
            }
            else
            {
                con.rollback();
            }

            con.setAutoCommit(true);
        }
        catch (Exception ex)
        {

            try
            {
                if (!con.isClosed())
                {
                    con.rollback();
                }
            }
            catch (SQLException ex1)
            {
            }
        }
        finally
        {
            try
            {
                if (st != null)
                {
                    st.close();
                }
                con.close();
            }
            catch (Exception ex)
            {
            }
        }

    }

    ///////////////////// FILES
    public static HashMap<String, String> materializarDocumentos()
    {
        Connection con = getConnection();
        HashMap<String, String> documentos = new HashMap<>();

        if (documentos_cache.size() > 0)
        {
            return documentos_cache;
        }

        if (con == null)
        {
            return documentos;
        }

        try
        {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM documento");

            while (rs.next())
            {
                String nombre = rs.getString("nombre");

                documentos.put(nombre, nombre);
            }

            documentos_cache = documentos;

            return documentos;
        }
        catch (Exception ex)
        {
            return documentos;
        }
    }

    public static boolean estaDocumentoProcesado(String nombreDoc)
    {
        Connection con = getConnection();
        HashMap<String, String> documentos = new HashMap<>();

        if (documentos_cache.size() > 0
            && documentos_cache.containsKey(nombreDoc)
            && documentos_cache.get(nombreDoc) != null)
        {
            return true;
        }

        if (con == null)
        {
            new NullPointerException("DB Connection is NULL").printStackTrace();
            return false;
        }

        try
        {
            PreparedStatement st = con.prepareStatement("SELECT * FROM documento WHERE nombre=?");
            st.setString(1, nombreDoc);
//            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                return true;
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
    
    public static void desmaterializarDocumentos(HashMap<String, String> documentos) throws IOException
    {
        Connection con = getConnection();
        PreparedStatement st = null;

        if (con == null)
        {
            return;
        }

        documentos_cache = documentos;

        try
        {
            boolean error = false;

            con.setAutoCommit(false);

            Statement st_delete = con.createStatement();
            st_delete.executeUpdate("DELETE FROM documento");

            st = con.prepareStatement("INSERT INTO documento (nombre) VALUES  (?)");

            for (Map.Entry<String, String> entry : documentos.entrySet())
            {
                if (error)
                {
                    break;
                }
                String key = entry.getKey();

                st.setString(1, key);

                try
                {
                    int resultado = st.executeUpdate();
                    if (resultado == 0)
                    {
                        error = true;
                    }
                }
                catch (Exception ex)
                {
                    error = true;
                }
            }

            if (!error)
            {
                con.commit();
            }
            else
            {
                con.rollback();
            }

            con.setAutoCommit(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                if (!con.isClosed())
                {
                    con.rollback();
                }
            }
            catch (SQLException ex1)
            {
                ex1.printStackTrace();
            }
        }
        finally
        {
            try
            {
                if (st != null)
                {
                    st.close();
                }
                con.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }

}
