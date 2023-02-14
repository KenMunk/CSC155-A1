package code;

//java stuff
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//jogamp stuff
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;

//math
import java.lang.Math;

public class Code extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];

	//Offsets
	private float x = 0.0f;
	private float inc = 0.01f;
	private float orbitAngle = 0;
	private float orbitIncrement = 1f;
	
	//Toggles
	private int colorToggle = 0;
	private int orbitToggle = 0;
	private int orientationToggle = 0;
	private float scrollToggle = 1f;
	
	private long lastTime = 0;

	/*
		Code for the following granular concepts:
			GLSL Matrix operations
			Button Inputs
			JPanel transparency (Fail)
			Key inputs
			Uniform variable assignment
			
		Obtained via ChatGPT
		Unfortunately it does not provide
		source documentation in its responses.
		
		Conversation history with the AI can
		be compiled and presented upon request.
	*/

	public Code()
	{	setTitle("Chapter 2 - program 6");
		setSize(400, 200);
		
		
		
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(createColorButton());
		buttonPanel.add(createOrbitButton());
		buttonPanel.setOpaque(false);
		buttonPanel.setBackground(new Color(0, 0, 0, 0));
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		InputMap inputMap = buttonPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = buttonPanel.getActionMap();

		KeyStroke key1 = KeyStroke.getKeyStroke('1');
		inputMap.put(key1, "point up");

		actionMap.put("point up", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				orientationToggle = 0;
			}
		});
		
		KeyStroke key2 = KeyStroke.getKeyStroke('2');
		inputMap.put(key2, "point left");

		actionMap.put("point left", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				orientationToggle = 1;
			}
		});
		
		KeyStroke key3 = KeyStroke.getKeyStroke('3');
		inputMap.put(key3, "point down");

		actionMap.put("point down", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				orientationToggle = 2;
			}
		});
		
		KeyStroke key4 = KeyStroke.getKeyStroke('4');
		inputMap.put(key4, "point right");

		actionMap.put("point right", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				orientationToggle = 3;
			}
		});
		
		buttonPanel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e){
				if(e.getUnitsToScroll() < 0){
					scrollToggle+=0.05f;
				}
				else{
					scrollToggle-=0.05f;
				}
			}
		});
		
		
		myCanvas.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e){
				if(e.getUnitsToScroll() < 0){
					scrollToggle+=0.05f;
				}
				else{
					scrollToggle-=0.05f;
				}
			}
		});
		
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}
	
	private JButton createColorButton(){
		
		JButton colorButton = new JButton("Color Toggle");
		
		colorButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				colorToggle++;
				colorToggle%=2;
				
			}
			
		});
		
		return(colorButton);
		
	}
	
	private JButton createOrbitButton(){
		
		JButton orbitButton = new JButton("Orbit Toggle");
		
		orbitButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				orbitToggle++;
				orbitToggle%=2;
				
			}
			
		});
		
		return(orbitButton);
		
	}
	
	private void stateUpdate(long count){
		
		x += inc;
		if (x > 1.0f) inc = -0.01f;
		if (x < -1.0f) inc = 0.01f;
		
		orbitAngle += orbitIncrement;
		orbitAngle %= 360;
		
		if(count > 0){
			stateUpdate(count-1);
		}
	}

	private void stateTimer(){
		//Elapsed time calculator
		if(lastTime == 0){
			lastTime = System.currentTimeMillis();
		}
		else{
			long elapsed = lastTime - System.currentTimeMillis();
			lastTime = System.currentTimeMillis();
			stateUpdate((elapsed/17)+1);
		}
	}
	
	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);
		
		stateTimer();
		
		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "offset");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, x);
		
		//consider making a utility function 
		//that will handle uniform value assignment
		//int 
		
		int scaleStateLoc = gl.glGetUniformLocation(renderingProgram, "scaleState");
		gl.glProgramUniform1f(renderingProgram, scaleStateLoc, scrollToggle);
		
		int orientationStateLoc = gl.glGetUniformLocation(renderingProgram, "orientationState");
		gl.glProgramUniform1i(renderingProgram, orientationStateLoc, orientationToggle);
		
		int rotationCounterLoc = gl.glGetUniformLocation(renderingProgram, "orbitCounter");
		gl.glProgramUniform1f(renderingProgram, rotationCounterLoc, orbitAngle);
		
		//Condition updates
		
		int colorConditionLoc = gl.glGetUniformLocation(renderingProgram, "colorCondition");
		gl.glProgramUniform1i(renderingProgram, colorConditionLoc, colorToggle);
		
		int orbitConditionLoc = gl.glGetUniformLocation(renderingProgram, "orbiting");
		gl.glProgramUniform1i(renderingProgram, orbitConditionLoc, orbitToggle);
		
		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}
	
	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("code/vertShader.glsl", "code/fragShader.glsl");
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
	}

	public static void main(String[] args) { new Code(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}
}