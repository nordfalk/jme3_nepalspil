/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Node;

/**
 *
 * @author j
 */
class Spiller {

    public final Node brik;
    public final String navn;
    int feltNr;

    Spiller(Node laxmiBrik, String laxmi) {
        brik = laxmiBrik;
        navn = laxmi;
    }
    
}
