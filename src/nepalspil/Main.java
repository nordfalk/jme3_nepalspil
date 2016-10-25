package nepalspil;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Arrays;
import nepalspil.kontrol.BrikRoterKontrol;

/**
 *
 * @author Jacob Nordfalk
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

    /*
    // Nødvendig?
    @Override
    public void setSettings(AppSettings settings) {
        settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        settings.setAudioRenderer(AppSettings.ANDROID_MEDIAPLAYER);
        System.out.println("XXX setSettings "+settings);
        super.setSettings(settings); 
    }
     */

    private Node lavSpillerbrik(Texture billede) {
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

        Node fodOgBilledeNode = new Node(billede.getName());
        fodOgBilledeNode.attachChild(billedeNode);
        fodOgBilledeNode.attachChild(fod);
        fodOgBilledeNode.scale(0.5f);
        return fodOgBilledeNode;
    }
    
    ArrayList<Spatial> felter = new ArrayList<>();
    ArrayList<Spiller> spillere = new ArrayList<>();

    @Override
    public void simpleInitApp() {
        Node manojBrik = lavSpillerbrik(assetManager.loadTexture("Textures/klippet-manoj.png"));
        Node laxmiBrik = lavSpillerbrik(assetManager.loadTexture("Textures/klippet-laxmi.png"));
        Node abishakBrik = lavSpillerbrik(assetManager.loadTexture("Textures/klippet-abishak.png"));
        Node bishalBrik = lavSpillerbrik(assetManager.loadTexture("Textures/klippet-bishal.png"));
        abishakBrik.rotate(0, 10, 0).scale(0.6f);
        bishalBrik.rotate(0, 10, 0).scale(0.6f);;
        abishakBrik.getLocalTranslation().x += 2;
        bishalBrik.getLocalTranslation().x -= 3;
        
        spillere.addAll(Arrays.asList(
                new Spiller(manojBrik, "Manoj"),
                new Spiller(laxmiBrik, "Laxmi"),
                new Spiller(abishakBrik, "Abishak"),
                new Spiller(bishalBrik, "Bishal")));
        rootNode.attachChild(manojBrik);
        rootNode.attachChild(laxmiBrik);
        rootNode.attachChild(abishakBrik);
        rootNode.attachChild(bishalBrik);


        Spatial scene = assetManager.loadModel("Scenes/spilScene.j3o");
        rootNode.attachChild(scene);

        for (int i = 1;; i++) {
            Spatial felt = rootNode.getChild("Felt" + i);
            if (felt == null) {
                break;
            }
            felter.add(felt);
        }
        System.out.println("felter= " + felter);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        DirectionalLight l = (DirectionalLight) scene.getLocalLightList().get(0); //  new DirectionalLight();
        //l.setDirection(new Vector3f(0.5973172f, -0.16583486f, 0.7846725f));
        //l.setDirection(new Vector3f(-1, -1, -1));

        int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(l);
        //dlsr.setLambda(0.055f);
        dlsr.setShadowIntensity(0.5f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
        viewPort.addProcessor(dlsr);

        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.setCullHint(Spatial.CullHint.Never);
        // Ryk kameraet op og til siden
        cam.setLocation(cam.getLocation().add(2, 3, -3));
        cam.lookAt(new Vector3f(), new Vector3f(0, 1, 0)); // peg det ind på spillepladen
        flyCam.setMoveSpeed(25);

        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("shoot") && !keyPressed) {
                makeCannonBall();
            }
        }
    };

    private BulletAppState bulletAppState;

    /**
     * This method creates one individual physical cannon ball. By defaul, the
     * ball is accelerated and flies from the camera position in the camera direction.
     */
    public void makeCannonBall() {
        /**
         * Create a cannon ball geometry and attach to scene graph.
         */
        Geometry ball_geo = new Geometry("cannon ball", new Box(0.1f, 0.1f, 0.1f));
        ball_geo.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
        rootNode.attachChild(ball_geo);
        /**
         * Position the cannon ball
         */
        ball_geo.setLocalTranslation(cam.getLocation());
        /**
         * Make the ball physcial with a mass > 0.0f
         */
        RigidBodyControl ball_phy = new RigidBodyControl(1f);
        /**
         * Add physical ball to physics space.
         */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        /**
         * Accelerate the physcial ball to shoot it.
         */
        ball_phy.setLinearVelocity(cam.getDirection().mult(25).add(0, 5, 0));
    }

    float tidTilRyk = 1;
    
    @Override
    public void simpleUpdate(float tpf) {

        tidTilRyk = tidTilRyk - tpf;
        if (tidTilRyk < 0) {
            System.out.println("Tid til at rykke!");
            tidTilRyk = 0.5f;

            Spiller sp = spillere.get((int) (Math.random() * spillere.size()));
            int slag = 1 + (int) (6 * Math.random());

            sp.feltNr = (sp.feltNr + slag) % felter.size();
            sp.ryk.startRykTil(felter.get(sp.feltNr));
            if (slag==6) sp.node.getControl(BrikRoterKontrol.class).roterEtSekund();
        }
    }
    


    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
