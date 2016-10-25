/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nepalspil;

import nepalspil.kontrol.BrikRoterKontrol;
import com.jme3.math.Transform;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import nepalspil.kontrol.BrikRykKontrol;

/**
 *
 * @author j
 */
class Spiller {

    public final Node node;
    public final String navn;
    int feltNr;
    BrikRykKontrol ryk;

    Spiller(Node laxmiBrik, String laxmi) {
        node = laxmiBrik;
        navn = laxmi;
        ryk = new BrikRykKontrol();
        ryk.setEnabled(false);
        node.addControl(ryk);
        node.addControl(new BrikRoterKontrol());
    }
    
}
