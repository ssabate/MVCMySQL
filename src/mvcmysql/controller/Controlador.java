/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvcmysql.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import mvcmysql.model.Model;
import mvcmysql.model.TaulaActors;
import mvcmysql.view.VistaActors;

/**
 *
 * @author profe
 */
public class Controlador {

    private Model odb;
    private VistaActors vista;
    private int filasel=-1;
    private String nom = "";
    private String cognom = "";
    private int id = -1;

    public Controlador(Model odb, VistaActors jf) {
        this.odb = odb;
        this.vista = jf;
        carregaTaula(odb.llistarActors(), TaulaActors.class);
        borrarCamps();
        vista.setVisible(true);
        control();
    }
    
    public void borrarCamps() {
        //Posem en blanc el nom i cognom de l'actor
        vista.getjTextField1().setText("");
        vista.getjTextField2().setText("");
        nom=cognom="";
    }

    public void carregaTaula(ArrayList resultSet, Class<?> classe) {
        // TODO add your handling code here:
        //Quan tornem a carregar la taula perdem la selecció que haviem fet i posem filasel a -1
        filasel = -1;

        Vector columnNames = new Vector();
        Vector data = new Vector();
        DefaultTableModel model;

        //Anotem el nº de camps de la classe
        Field[] camps = classe.getDeclaredFields();
        //Ordenem els camps alfabèticament
        Arrays.sort(camps, new OrdenarCampClasseAlfabeticament());
        int ncamps = camps.length;
                //Recorrem els camps de la classe i posem els seus noms com a columnes de la taula
        //Com hem hagut de posar _numero_ davant el nom dels camps, mostrem el nom a partir de la 4ª lletra 
        for (Field f : camps) {
            columnNames.addElement(f.getName().substring(3));
        }
        //Si hi ha algun element a l'arraylist omplim la taula
        if (resultSet.size() != 0) {

            //Guardem els descriptors de mètode que ens interessen (els getters)
            Vector<Method> methods = new Vector(resultSet.size());
            try {

                PropertyDescriptor[] descriptors = Introspector.getBeanInfo(classe).getPropertyDescriptors();
                Arrays.sort(descriptors, new OrdenarMetodeClasseAlfabeticament());
                for (PropertyDescriptor pD : descriptors) {
                    Method m = pD.getReadMethod();
                    if (m != null & !m.getName().equals("getClass")) {
                        methods.addElement(m);
                    }
                }

            } catch (IntrospectionException ex) {
                Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (Object m : resultSet) {
                Vector row = new Vector(ncamps);

                for (Method mD : methods) {
                    try {
                        row.addElement(mD.invoke(m));
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

//               for(Field f:classe.getDeclaredFields())
//                    try {
//                        row.addElement(f.get(m));
//                   } catch (IllegalArgumentException ex) {
//                       Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
//                   } catch (IllegalAccessException ex) {
//                       Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
//                   }
                data.addElement(row);
            }
        }

        model = new DefaultTableModel(data, columnNames);
        vista.getjTable2().setModel(model);

        TableColumn column;
        for (int i = 0; i < vista.getjTable2().getColumnCount(); i++) {
            column = vista.getjTable2().getColumnModel().getColumn(i);
            column.setMaxWidth(250);
        }

    }
    
    // Classe "niada" (nested class, clase anidada) usada per ordenar els camps de les classes alfabèticament
    public static class OrdenarCampClasseAlfabeticament implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            return (int) (((Field) o1).getName().compareToIgnoreCase(((Field) o2).getName()));
        }
    }

    // Classe "niada" (nested class, clase anidada) usada per ordenar els mètodes de les classes alfabèticament
    public static class OrdenarMetodeClasseAlfabeticament implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {

            Method mo1 = ((PropertyDescriptor) o1).getReadMethod();
            Method mo2 = ((PropertyDescriptor) o2).getReadMethod();

            if (mo1 != null && mo2 != null) {
                return (int) mo1.getName().compareToIgnoreCase(mo2.getName());
            }
            if (mo1 == null) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    
    
   /* public void carregaTaula(ArrayList resultSet) {
        // TODO add your handling code here:
        //Quan tornem a carregar la taula perdem la selecció que haviem fet i posem filasel a -1
        filasel = -1;

        //Si hi ha algun element a l'arraylist omplim la taula
        if (resultSet.size() != 0) {
            Vector columnNames = new Vector();
            Vector data = new Vector();
            DefaultTableModel model;

            //Obtenim la classe dels objectes de la llista
            Class<?> classe = resultSet.get(0).getClass();
            //Anotem el nº de camps de la classe
            int ncamps = classe.getDeclaredClasses().length;
            //Recorrem els camps de la classe i posem els seus noms com a columnes de la taula
            for (Field f : classe.getDeclaredFields()) {
                columnNames.addElement(f.getName());
            }

            //Guardem els descriptors de mètode que ens interessen (els getters)
            Vector<Method> methods = new Vector(resultSet.size());
            try {
                for (PropertyDescriptor pD : Introspector.getBeanInfo(classe).getPropertyDescriptors()) {
                    Method m = pD.getReadMethod();
                    if (m != null & !m.getName().equals("getClass")) {
                        methods.addElement(m);
                    }
                }
            } catch (IntrospectionException ex) {
                Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (Object m : resultSet) {
                Vector row = new Vector(ncamps);

                for (Method mD : methods) {
                    try {
                        row.addElement(mD.invoke(m));
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

//               for(Field f:classe.getDeclaredFields())
//                    try {
//                        row.addElement(f.get(m));
//                   } catch (IllegalArgumentException ex) {
//                       Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
//                   } catch (IllegalAccessException ex) {
//                       Logger.getLogger(VistaActors.class.getName()).log(Level.SEVERE, null, ex);
//                   }
                data.addElement(row);
            }

            model = new DefaultTableModel(data, columnNames);
            vista.getjTable2().setModel(model);

            TableColumn column;
            for (int i = 0; i < vista.getjTable2().getColumnCount(); i++) {
                column = vista.getjTable2().getColumnModel().getColumn(i);
                column.setMaxWidth(250);
            }
        }

    }
*/
    
    public void control() {
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.getSource().equals(vista.getjButton6())) {
                    if (filasel!=-1){
                            odb.borrarActor(id);
                            borrarCamps();
                            carregaTaula(odb.llistarActors(), TaulaActors.class);
                    }
                    else JOptionPane.showMessageDialog(null, "Per borrar un actor primer l'has de seleccionar!!", "Error", JOptionPane.ERROR_MESSAGE);                
                } 
                else 
                    if (actionEvent.getSource().equals(vista.getjButton5())) {
                        if (!nom.equals("") || !cognom.equals("")) {
                            odb.insertarActor(nom, cognom);
                            borrarCamps();
                            carregaTaula(odb.llistarActors(), TaulaActors.class);
                        }
                        else JOptionPane.showMessageDialog(null, "No pots introduir un actor sense nom ni cognom!!", "Error", JOptionPane.ERROR_MESSAGE);
                    } 
                    else
                        if (actionEvent.getSource().equals(vista.getjButton7())) {
                            if (filasel!=-1 && (!nom.equals("") || !cognom.equals(""))){
                                odb.modificarActor(id, nom, cognom);
                                borrarCamps();
                                carregaTaula(odb.llistarActors(), TaulaActors.class);
                            }
                            else JOptionPane.showMessageDialog(null, "Per modificar un actor primer l'has de seleccionar i posar algun valor al nom i/o cognoms!!", "Error", JOptionPane.ERROR_MESSAGE);                
                        }
                        else {
                            try {
                                    odb.finalize();
                            } catch (Throwable ex) {
                                System.out.println("Error tancant la base de dades!!");
                            }
                            System.exit(0);
                        }
            }
        };
        vista.getjButton4().addActionListener(actionListener);
        vista.getjButton5().addActionListener(actionListener);
        vista.getjButton6().addActionListener(actionListener);
        vista.getjButton7().addActionListener(actionListener);
        
        MouseAdapter mouseAdapter=new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
                
                try {
                    filasel = vista.getjTable2().getSelectedRow();
                    if (filasel != -1) {
                        //if(jTable2.getValueAt(filasel, 0) instanceof String ) 
                        id = Integer.parseInt(vista.getjTable2().getValueAt(filasel, 0).toString());
                        //else id=(int)jTable2.getValueAt(filasel, 0);
                        nom = (String) vista.getjTable2().getValueAt(filasel, 1);
                        vista.getjTextField1().setText(nom);
                        cognom = (String) vista.getjTable2().getValueAt(filasel, 2);
                        vista.getjTextField2().setText(cognom);
                    }else borrarCamps();
                } catch (NumberFormatException ex) {
                }
            }
        
        };
        vista.getjTable2().addMouseListener(mouseAdapter);
        
        FocusAdapter focusAdapter=new FocusAdapter(){
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e); //To change body of generated methods, choose Tools | Templates.
                if(e.getSource().equals(vista.getjTextField1())){
                    nom = vista.getjTextField1().getText().trim();
                }
                if(e.getSource().equals(vista.getjTextField2())){
                    cognom = vista.getjTextField2().getText().trim();
                }
            }
        
        };
        
        vista.getjTextField1().addFocusListener(focusAdapter);
        vista.getjTextField2().addFocusListener(focusAdapter);
        
        WindowAdapter windowAdapter =new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                //Mateix codi que quan paretem el botó de sortir del programa
                try {
                    odb.finalize();
                } catch (Throwable ex) {
                    System.out.println("Error tancant la base de dades!!");
                }
                System.exit(0);
            }
        };
        
        vista.addWindowListener(windowAdapter);
    }

}
