package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);

        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Nepalspillet - Jacob Nordfalk");
        settings.put("VSync", true);
        settings.put("Samples", 4); //Anti-Aliasing
        app.setSettings(settings);
        
        app.start();
    }

    ArrayList<Spatial> felter = new ArrayList<>();
    ArrayList<Spiller> spillere = new ArrayList<>();
    
    @Override
    public void simpleInitApp() {

        Node laxmiBrik = lavBrik(assetManager.loadTexture("Textures/klippet-laxmi.png"));
        Node abishakBrik = lavBrik(assetManager.loadTexture("Textures/klippet-abishak.png"));
        Node bishalBrik = lavBrik(assetManager.loadTexture("Textures/klippet-bishal.png"));
        abishakBrik.rotate(0, 10, 0).scale(0.6f);
        bishalBrik.rotate(0, 10, 0).scale(0.6f);;
        abishakBrik.getLocalTranslation().x += 2;
        bishalBrik.getLocalTranslation().x -= 3;
        
        spillere.addAll(Arrays.asList(
            new Spiller(laxmiBrik, "Laxmi"),
            new Spiller(abishakBrik, "Abishak"),
            new Spiller(bishalBrik, "Bishal")));       
        
        rootNode.attachChild(assetManager.loadModel("Scenes/spilScene.j3o"));

        //Spatial f1 = rootNode.getChild("Felt1");
        //abishakBrik.setLocalTranslation(f1.getWorldTranslation());
        
        for (int i=1; ; i++) {
            Spatial felt = rootNode.getChild("Felt"+i);
            System.out.println("felt= "+ felt);
            if (felt==null) break;
            felt.setUserData("nummer", i);
            felter.add(felt);
        }
        System.out.println("felter= "+ felter);
                
        rootNode.attachChild(laxmiBrik);
        rootNode.attachChild(abishakBrik);
        rootNode.attachChild(bishalBrik);

        // Ryk kameraet op og til siden
        cam.setLocation( cam.getLocation().add(2, 3, -3));
        cam.lookAt(new Vector3f(), new Vector3f(0, 1, 0)); // peg det ind på spillepladen
    }

    private Node lavBrik(Texture billede) {
        Material fodMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fodMat.setTexture("ColorMap", assetManager.loadTexture("Textures/dirt.jpg"));
        Spatial fod = assetManager.loadModel("Models/nepalbrik-fod/nepalbrik-fodfbx.j3o");
        fod.setMaterial(fodMat);
        
        Geometry brikGeom = new Geometry("Brikbillede", new Box(1, 2, 0.1f));        
        Node billedeNode = new Node();
        billedeNode.attachChild(brikGeom);
        billedeNode.setLocalTranslation(0, 3, 0);
        Material laxmiMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        laxmiMat.setTexture("ColorMap", billede);
        brikGeom.setMaterial(laxmiMat);
        
        Node fodOgBilledeNode = new Node();
        fodOgBilledeNode.attachChild(billedeNode);
        fodOgBilledeNode.attachChild(fod);
        fodOgBilledeNode.scale(0.5f);
        fodOgBilledeNode.getLocalTranslation().z += 2.5f;
        return fodOgBilledeNode;
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        if (this.timer.getTime() % 50 == 0) {
            Spiller sp  = spillere.get((int) (Math.random()*spillere.size()));
            sp.feltNr = (sp.feltNr+1) % felter.size();
            if (sp.feltNr == 0) {
                System.out.println("Hurra spilleren er færdig! " + sp.navn );
                Spatial felt = rootNode.getChild("Målfelt");
                sp.brik.setLocalTranslation(felt.getLocalTranslation());
                sp.brik.setLocalRotation(felt.getLocalRotation());
            } else {
                Spatial felt = felter.get(sp.feltNr);
                sp.brik.setLocalTranslation(felt.getLocalTranslation());
                sp.brik.setLocalRotation(felt.getLocalRotation());
                System.out.println("Rykker "+sp.navn+" til "+felt);                
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
