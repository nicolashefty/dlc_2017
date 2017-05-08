/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dlc.buscador.modelo.vectorial.entidades.db;

import java.util.*;
import javax.naming.*;
import javax.sql.*;

/**
 *
 * @author nicolashefty
 */
public class Database
{
    private static final String DB_POOL = "jdbc/DLCPool";
    protected static DataSource getConnection()
    {
        try
        {
//            Properties prop = new Properties();
//            prop.setProperty(Context.INITIAL_CONTEXT_FACTORY,
//                             "com.sun.enterprise.naming.impl.SerialInitContextFactory");
//            prop.setProperty(Context.URL_PKG_PREFIXES,
//                             "com.sun.enterprise.naming");
//            prop.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
//            prop.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
            Context initCtx = new InitialContext();
//            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            return (DataSource) initCtx.lookup(DB_POOL);
        }
        catch (NamingException ex)
        {
            System.out.println("No se pudo resolver JNDI "+
                    ex.toString());
        }
        return null;
    }
}
