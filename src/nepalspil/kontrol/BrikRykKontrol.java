/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nepalspil.kontrol;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author j
 */
public class BrikRykKontrol extends AbstractControl {
    
    float interpolation = 1;
    Transform fraT = new Transform();
    Transform tilT = new Transform();
    private Vector3f fraV;
    private Vector3f tilV;
    private Vector3f midtV = new Vector3f();
    
    public void startRykTil(Spatial tilFelt) {
        // kloning og tildeling laver nye kopier af objekterne, så i stedet kopierer vi værdierne ind i forudoprettede opjekter
        fraT.set(spatial.getLocalTransform());
        tilT.set(tilFelt.getLocalTransform()); 
        // Variér til-position lidt
        tilT.getTranslation().addLocal(FastMath.rand.nextFloat() / 5 - 0.1f, 0, FastMath.rand.nextFloat() / 5 - 0.1f);

        fraV = fraT.getTranslation();
        tilV = tilT.getTranslation();
        float højdeAfHop = fraV.distance(tilV)/4;
        midtV.set(fraV).interpolateLocal(tilV, 0.5f).addLocal(0, højdeAfHop, 0);
        
        interpolation = 0;
        setEnabled(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        interpolation += 3*tpf;
        if (interpolation > 1) {
            tpf = interpolation - 1;
            interpolation = 1;
            setEnabled(false);
        }
        float inter = interpolation;
        inter = (inter * inter);
        //inter = (inter*inter*inter + 1-(1-inter)*(1-inter)*(1-inter))/2;
        //System.out.printf("interpolation=%.2f  inter=%.2f\n", interpolation, inter);
        Transform brikT = spatial.getLocalTransform();
        FastMath.interpolateBezier(inter, fraV, midtV, midtV, tilV, brikT.getTranslation());        
        brikT.getRotation().slerp(fraT.getRotation(), tilT.getRotation(), inter);
        spatial.setLocalTransform(brikT);

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
