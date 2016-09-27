/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Transform;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author j
 */
class Spiller {

    public final Node node;
    public final String navn;
    int feltNr;
    Transform rykFra;
    Transform rykTil;

    Spiller(Node laxmiBrik, String laxmi) {
        node = laxmiBrik;
        navn = laxmi;
    }
    
}
