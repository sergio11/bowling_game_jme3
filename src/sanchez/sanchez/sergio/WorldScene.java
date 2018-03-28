
package sanchez.sanchez.sergio;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * @author Sergio Sánchez Sánchez
 */
public class WorldScene {
   
    private Material ballsMat, wallMat, floorMat, boloMat;
    
    private final AssetManager assetManager;
    private final Node rootNode;
    private final BulletAppState  bulletAppState;
    private final Camera cam;

    /**
     * 
     * @param assetManager 
     * @param rootNode 
     * @param bulletAppState 
     * @param cam 
     */
    public WorldScene(final AssetManager assetManager, final Node rootNode, 
            final BulletAppState  bulletAppState, final Camera cam) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.bulletAppState = bulletAppState;
        this.cam = cam;
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
        rockyTexture.setWrap(Texture.WrapMode.Repeat);
     
        wallMat.setTexture("DiffuseMap", rockyTexture);
        
        floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        // Set a standard rocky texture.
        final TextureKey grassFloorTextureKey = new TextureKey("Textures/GrassFloor.jpg");
        grassFloorTextureKey.setGenerateMips(true);
        
        final Texture grassFloorTexture = assetManager.loadTexture(grassFloorTextureKey);
        grassFloorTexture.setWrap(Texture.WrapMode.Repeat);
     
        floorMat.setTexture("DiffuseMap", grassFloorTexture);
        
        // We create the material of the ball 
        // from an illuminated base and make it reflective blue.
        ballsMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
        ballsMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/BallMp12.jpg"));

        ballsMat.setFloat("Shininess", 1);
       
        boloMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        boloMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/bowling.jpg"));
        boloMat.setFloat("Shininess", 1);
 
    }
    
    
    /**
     * Show Light
    */
    private void showLight(){
    
        SpotLight foco = new SpotLight();
        foco.setSpotRange(100f);  // distancia
        // ángulos del cono: (si los ponemos en grados hay que
        // multiplicar por la constante FastMath.DEG_TO_RAD)
        foco.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD);
        foco.setSpotOuterAngle(45f * FastMath.DEG_TO_RAD);
        // color blanco pero intensificado un 20% para que brille más
        foco.setColor(ColorRGBA.White.mult(2f));
        // posición y dirección: la de la cámara por defecto
        foco.setPosition(cam.getLocation());
        foco.setDirection(cam.getDirection());
        rootNode.addLight(foco);
        
    }
    

        
    /**
     * Play Ambient Sound
     */
    private void playAmbientSound() {

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
     * Num of rows.
     * @param rows 
     */
    public void showBowling(final Integer rows) {
        
        // Start Position on x-axis
        final Float startPosition = 2.0f;
        // Start Position on z-axis
        final Integer initialDistance = -8; 
        // Bowling Separation
        final Float bowlingSeparation = -1.0f;
        // separation on the z-axis
        final Integer distanceSeparation = 1;
   
        final Float separationRow = -0.5f;
        
        for(int i = rows, s = 0; i > 0; i--, s++){
            
            final Float initialPosition = startPosition + (s * separationRow);
            
            for(int j=0; j < i; j++){
                
                final Geometry boloGeo = new Geometry("Bolo", 
                    new Cylinder(10, 15, 0.30f, 2.5f, true));
                
                boloGeo.setLocalTranslation(
                        initialPosition + (bowlingSeparation * j) , 
                        0.75f, 
                        initialDistance + ( distanceSeparation * s));
                
                // Vertical
                boloGeo.rotate((float) Math.PI / -2.0f, 0, 0);
                // Configure material
                boloGeo.setMaterial(boloMat);
                
                rootNode.attachChild(boloGeo);
                
                final RigidBodyControl rigidBodyControl = new RigidBodyControl(1f);
                boloGeo.addControl(rigidBodyControl);
                //Agregamos gravedas
                bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -6, 0));
                bulletAppState.getPhysicsSpace().add(rigidBodyControl);
            }
        }
 
    }
    
    /**
     * Create Ball
     */
    public void throwBall() {
        
        // Crear una esfera de 40 centímetros de diámetro
        Sphere esfera = new Sphere(32, 32, .7f);

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
        
        AudioNode lanzamiento = new AudioNode(assetManager, "Sound/Effects/Bang.wav", false);
        lanzamiento.setVolume(0.7f); //Volumen
        rootNode.attachChild(lanzamiento);
        lanzamiento.play();
    }
    
  
    
    /**
     * Show World
     */
    public void show() {
       
        // Create the materials
        loadMaterials();
        
        // Create floow
        showFloor();

        // Create wall
        showWall();

        // Create light
        showLight();
       
        // reproduce the ambient sound
        playAmbientSound();
        
        // Show Six Rows
        showBowling(6);
    }
    
}
