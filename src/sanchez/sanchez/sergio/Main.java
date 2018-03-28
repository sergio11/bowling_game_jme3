package sanchez.sanchez.sergio;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * Bowling game
 * @author Sergio Sánchez Sánchez
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    private BulletAppState  bulletAppState;
    private Material        ballsMat, wallMat, floorMat;
    
    /**
     * Load Materials
     */
    private void loadMaterials() {
        
        // We create the material of the wall and the floor from a predefined texture, 
        // causing it to repeat itself by its surface
        // It's in the jme3-core.jar like all of the other built-in assets.
        wallMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        // Set a standard rocky texture.
        TextureKey key = new TextureKey("Textures/StoneWall.jpg");
        key.setGenerateMips(true);
        
        Texture rockyTexture = assetManager.loadTexture(key);
        rockyTexture.setWrap(WrapMode.Repeat);
     
        wallMat.setTexture("DiffuseMap", rockyTexture);
        
        floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        // Set a standard rocky texture.
        final TextureKey grassFloorTextureKey = new TextureKey("Textures/GrassFloor.jpg");
        grassFloorTextureKey.setGenerateMips(true);
        
        final Texture grassFloorTexture = assetManager.loadTexture(grassFloorTextureKey);
        grassFloorTexture.setWrap(WrapMode.Repeat);
     
        floorMat.setTexture("DiffuseMap", grassFloorTexture);
        
        // We create the material of the ball 
        // from an illuminated base and make it reflective blue.
        ballsMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
        ballsMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/BallMp12.jpg"));

        ballsMat.setFloat("Shininess", 1);
 
    }
    
    /**
     * Show Floor
     */
    private void showFloor(){
        
        // Para hacer el suelo usaremos estas variables:
        // floor    : la forma del suelo
        // suelo_mat: el material del suelo
        // floorGeo: el spatial de tipo geometría que contendrá el suelo
        // floorPhy: el objeto de control de físicas que está asociado al suelo

        // Creamos una forma de caja de 5 metros de ancho,
        // 10 de largo y 10cm de alto.
        Box floor = new Box(Vector3f.ZERO, 20f, 0.1f, 15f);

        // ajustamos el tamaño de la textura que le dará aspecto de suelo
        // para que no se deforme
        floor.scaleTextureCoordinates(new Vector2f(6, 3));

        // Creamos un spatial de tipo geometría para asociarlo al suelo
        Geometry floorGeo = new Geometry("Floor", floor);
        // asignamos el material
        floorGeo.setMaterial(floorMat);
        // bajamos el suelo 10cm para que el origen esté un poco por encima
        floorGeo.setLocalTranslation(0, -0.1f, 0);
        floorGeo.rotate(0, FastMath.DEG_TO_RAD * 90, 0);
        // y, finalmente, lo incluimos en el grafo de escena
        rootNode.attachChild(floorGeo);

        // Crearemos un objeto de control físico para asociarlo al suelo
        // IMPORTANTE: tiene masa 0 para convertirlo en un objeto estático
        RigidBodyControl floorPhy = new RigidBodyControl(0.0f);
        // asociamos el objeto de control a la geometría del suelo
        floorGeo.addControl(floorPhy);
        // y añadimos el objeto de control al motor de físicas
        bulletAppState.getPhysicsSpace().add(floorPhy);
    
    }
    
    /**
     * Show Wall
     */
    public void showWall(){
        
        // Para hacer la pared usaremos estas variables:
        // wall    : la forma de la pared
        // wallGeo: el spatial de tipo geometría que contendrá la pared
        // wallPhy: el objeto de control de físicas que está asociado 

        // Creamos una forma de caja de 5 metros de ancho,
        // 10 de largo y 10cm de alto.
        Box wall = new Box(Vector3f.ZERO, 8f, 0.4f, 20f);

        // Ajustamos el tamaño de la textura que le dará el aspecto deseado
        // para que no se deforme
        wall.scaleTextureCoordinates(new Vector2f(6, 3));

        // Creamos un spatial de tipo geometría para asociarlo a la pared
        Geometry wallGeo = new Geometry("Wall", wall);
        // asignamos el material
        wallGeo.setMaterial(wallMat);
        
        // lo desplazamos detrás del suelo
        wallGeo.setLocalTranslation(0, -0.1f, -10);
        // inclinado 90 grados en el eje Y para ponerlo en vertical
        wallGeo.rotate(FastMath.DEG_TO_RAD * 90, 0, FastMath.DEG_TO_RAD * 90);
        wallGeo.move(-3.5f, 2, 0);
        // y, finalmente, lo incluimos en el grafo de escena
        rootNode.attachChild(wallGeo);

        // Crearemos un objeto de control físico para asociarlo a la pared
        // IMPORTANTE: tiene masa 0 para convertirlo en un objeto estático
        RigidBodyControl wallPhy = new RigidBodyControl(0.0f);
        // asociamos el objeto de control a la geometría de la pared
        wallGeo.addControl(wallPhy);
        // y añadimos el objeto de control al motor de físicas
        bulletAppState.getPhysicsSpace().add(wallPhy);
    
    }
    
    /**
     * Show Light
     */
    private void showLight(){
    
        // Crearemos una luz direccional que parezca venir de la parte
        // superior derecha del jugador, por detrás.
        DirectionalLight light = new DirectionalLight();

        // de color blanco y no excesivamente brillante
        light.setColor(ColorRGBA.White.mult(0.8f));

        // proveniente de la parte superior derecha de la posición inicial
        // del jugador, por detrás.
        light.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());

        // añadir la luz al grafo
        rootNode.addLight(light);
        
    }
    
    /**
     * Play Ambient Sound
     */
    public void playAmbientSound() {

        //Sonido ambiente
        AudioNode ambientSound = new AudioNode(assetManager, 
                "Sound/Environment/River.ogg", false);
        
        ambientSound.setLooping(true);
        // configure volume
        ambientSound.setVolume(0.2f);
        
        //Add the ambient sound to the graph of the scene
        rootNode.attachChild(ambientSound);
        
        // start
        ambientSound.play();

    }
    
    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Crear materiales
        loadMaterials();
        
        // Creamos el suelo
        showFloor();

        // Creamos la pared
        showWall();

        // Crear la luz
        showLight();

        // Colocamos la cámara en una posición adecuada para ver la superficie
        // del suelo y mirando hacia ella.
        cam.setLocation(new Vector3f(0, 6f, 20f));
        cam.lookAt(new Vector3f(0, 2, 0), Vector3f.UNIT_Y);

        // Ponemos un color de fondo azul oscuro
        viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 0.2f, 0));
        
        // Reproducir sonido ambiente
        playAmbientSound();
        
        // ¡Bola va!
        hazBola();
    }

    public void hazBola() {
        
        // Crear una esfera de 40 centímetros de diámetro
        Sphere esfera = new Sphere(32, 32, 0.4f);

        // Asociar la forma a una geometría nueva
        Geometry bola_geo = new Geometry("bola", esfera);
        // asignarle el material
        bola_geo.setMaterial(ballsMat);

        // añadirla al grafo de escena
        rootNode.attachChild(bola_geo);

        // la colocamos en la posición de la cámara
        bola_geo.setLocalTranslation(cam.getLocation());

        // Creamos el objeto de control físico asociado a la bola con un peso
        // de 1Kg.
        RigidBodyControl bola_fis = new RigidBodyControl(1f);
        // Asociar la geometría de la bola al control físico
        bola_geo.addControl(bola_fis);
        // Añadirla al motor de física
        bulletAppState.getPhysicsSpace().add(bola_fis);
        
        
        // ¡Empujar la bola en la dirección que mira la cámara a una velocidad
        // de 8 metros por segundo!
        bola_fis.setLinearVelocity(cam.getDirection().mult(8));
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