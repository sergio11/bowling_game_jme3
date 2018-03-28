package sanchez.sanchez.sergio;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

/**
 * Bowling game
 * @author Sergio Sánchez Sánchez
 */
public class Main extends SimpleApplication {
    
    private final String THROW_BALL_CMD = "throw_ball";
    private final String GRAVITY_CMD = "gravity_cmd";
    
    protected BulletAppState bulletAppState;
    protected WorldScene worldScene;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    /**
     * Key Board Listener
     */
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            
            if (name.equals(THROW_BALL_CMD) && !keyPressed) {
                worldScene.throwBall();
            }
            
            if (name.equals("Gravedad") && !keyPressed) {
                //Si esta activada la desactivamos y viceversa
                if (bulletAppState.isEnabled()) {
                    bulletAppState.setEnabled(false);
                } else {
                    bulletAppState.setEnabled(true);
                }
            }
        }
    };

    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

      
        // Colocamos la cámara en una posición adecuada para ver la superficie
        // del suelo y mirando hacia ella.
        cam.setLocation(new Vector3f(0, 6f, 20f));
        cam.lookAt(new Vector3f(0, 2, 0), Vector3f.UNIT_Y);

        // Ponemos un color de fondo azul oscuro
        viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 80f, 0));
        
        worldScene = new WorldScene(assetManager, rootNode, bulletAppState, cam);
        
        worldScene.show();
        
        
        //Teclas y botón del ratón
        KeyTrigger spacebar = new KeyTrigger(keyInput.KEY_SPACE);
        KeyTrigger teclaG = new KeyTrigger(keyInput.KEY_G);
        MouseButtonTrigger leftButton = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);

        inputManager.addMapping(THROW_BALL_CMD, spacebar, leftButton);
        inputManager.addMapping(GRAVITY_CMD, teclaG);

        inputManager.addListener(actionListener, THROW_BALL_CMD);
        inputManager.addListener(actionListener, GRAVITY_CMD);
        
        cam.clearViewportChanged();
    }

   
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}