/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.classes;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

/**
 *
 * @author mary
 */
public class Person {
    Long Id;
    String user;
    String lblPerson;

    public Person(Long Id, String user, String lblPerson) {
        this.Id = Id;
        this.user = user;
        this.lblPerson = lblPerson;
    }

     public Long getId() {
        return Id;
    }

    public String getLblPerson() {
        return lblPerson;
    }

    public String getUser() {
        return user;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public void setLblPerson(String lblPerson) {
        this.lblPerson = lblPerson;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }   

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
