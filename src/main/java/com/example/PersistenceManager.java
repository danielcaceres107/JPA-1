/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Danie
 */
public class PersistenceManager {
    public static final boolean DEBUG = true;
    private static final PersistenceManager singleton = new PersistenceManager();
    protected EntityManagerFactory emf;
    public static PersistenceManager getInstance() {
        return singleton;
    }
    
    private PersistenceManager() {
    }
    
    public EntityManagerFactory getEntityManagerFactory() {
    if (emf == null) {
        createEntityManagerFactory();
    }
    return emf;
    }
    
    public void closeEntityManagerFactory() {
        if (emf != null) {
        emf.close();
        emf = null;
            if (DEBUG) {
            System.out.println("Persistence finished at " + new java.util.Date());
            }
        }
    }
    protected void createEntityManagerFactory() {
        this.emf = Persistence.createEntityManagerFactory("CompetitorsPU", 
        System.getProperties());
        if (DEBUG) {
            System.out.println("Persistence started at " + new java.util.Date());
        }
    }
}