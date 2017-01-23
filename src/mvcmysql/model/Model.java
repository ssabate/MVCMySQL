/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mvcmysql.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
/**
 *
 * @author profe
 */
public class Model {
    
    private static Connection connexio=null;  
    private static ResultSet resultSet = null;
        
    public Model() {
        Properties props = new Properties();
        String url, user, password;
        url=user=password=null;
        
        try(FileInputStream in = new FileInputStream("database.properties")) {
            props.load(in);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
            crearConnexio(url, user, password);
        } catch (IOException ex) {
            System.err.println("No s'ha pogut establir la connexió a la BD...");
            System.exit(0);
        }   
    }

    public void finalize() throws Throwable {
        if(resultSet!=null) resultSet.close();
        if(connexio!=null) connexio.close();
        System.out.println("Tancant la connexió a la BD...");
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }


    private void crearConnexio(String url, String usuari, String password){
        try {
                connexio = DriverManager.getConnection(url, usuari, password);
                System.out.println("Connectant a la BD...");
        } catch (SQLException ex) {
            System.err.println("No s'ha pogut establir la connexió a la BD...");
            System.exit(0);
        }

    }
    
    public void insertarActor(String first_name, String last_name){
            
        String sql = "INSERT INTO actor (first_name, last_name) VALUES (? , ?)";
        try(PreparedStatement sentenciaPr=connexio.prepareStatement(sql)) {
            sentenciaPr.setString(1, first_name);
            sentenciaPr.setString(2, last_name);
            sentenciaPr.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error a l'insertar l'actor!!");
        }  
    
    }
    
    public void borrarActor(int actor_id){
            
        String sql = "DELETE FROM actor WHERE actor_id=?";
        try(PreparedStatement sentenciaPr=connexio.prepareStatement(sql)) {
            sentenciaPr.setInt(1, actor_id);
            sentenciaPr.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al borrar l'actor!!");
        }  
    
    }
    
    public void modificarActor(int actor_id, String first_name, String last_name){
            
        String sql = "UPDATE actor SET first_name=?, last_name=? "+
                "WHERE actor_id=?";
        try(PreparedStatement sentenciaPr=connexio.prepareStatement(sql)) {
            sentenciaPr.setString(1, first_name);
            sentenciaPr.setString(2, last_name);
            sentenciaPr.setInt(3, actor_id);
            sentenciaPr.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al modificar l'actor!!");
        }  
    
    }
    
    public ArrayList<TaulaActors> llistarActors(){
            
        ArrayList llista=new ArrayList();
        String sql = "SELECT actor_id, first_name, last_name FROM actor ORDER BY 1;";
        try(PreparedStatement sentenciaPr=connexio.prepareStatement(sql)) {
            this.resultSet=sentenciaPr.executeQuery();
            
            if(this.resultSet!=null){
            
                while(resultSet.next()){
                    int actor_id=resultSet.getInt(1);
                    String first_name=resultSet.getString(2);
                    String last_name=resultSet.getString(3);
                    llista.add(new TaulaActors(actor_id, first_name, last_name));                
                }
            
            
            }
        } catch (SQLException ex) {
            System.err.println("Error al llistar els actors!!");
        }  
        return llista;    
    }
    
    
    
}
