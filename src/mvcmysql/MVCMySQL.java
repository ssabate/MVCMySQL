/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mvcmysql;

import mvcmysql.view.VistaActors;
import mvcmysql.model.Model;
import mvcmysql.controller.Controlador;

/**
 *
 * @author profe
 */
public class MVCMySQL {
    
    static Model model=new Model();
    static VistaActors vista=new VistaActors();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Controlador(model, vista);
    }
    
}
