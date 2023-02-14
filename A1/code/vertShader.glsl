#version 430
#define M_PI 3.1415926535897932384626433832795

/*
	
	The goal here is to limit the amount of java code by
	shifting as many operations as possible here so that
	they are not impacting the performance of the java
	code base
	
	Change the triangle layout so that the nominal orientation
	is pointing the tip to the top of the screen
	
	Recipe:
	Rotate the triangle (orientation state) 0.5pi pi 1.5pi 0pi
	Scale the triangle
	Offset triangle location by orbit position
	Offset triangle by the back & forth motion
	
	define the grad_color here as the logic behind that
	will be defined in the fragment shader.  Although
	it may induce a performance hit.
	
	

//*/

uniform int orbiting;

uniform float orbitState;
uniform float orbitCounter;
uniform int orientationState;
uniform float scaleState;
uniform float offset;

out vec4 grad_color;

mat4 orientationMat;
mat4 scaleMat;
mat4 transMat;
mat4 orbitMat;
mat4 counterOrbitMat;
float radAngle;

void main(void)
{
	if (gl_VertexID == 0) gl_Position = vec4( 0.05,-0.25, 0.0, 1.0);
	else if (gl_VertexID == 1) gl_Position = vec4(0,0.25, 0.0, 1.0);
	else gl_Position = vec4( -0.05, -0.25, 0.0, 1.0);
	
	
	//Seeing if I can pull off the color gradiant without a single
	//surface if
	
	grad_color = vec4(
		mod(mod((1+gl_VertexID),3),2),
		mod(gl_VertexID,2),
		mod(mod((gl_VertexID+2),3),2),
		1
	);
	
	radAngle = mod(orbitCounter,360)*M_PI/180;
	
	orbitMat = mat4(
		vec4(cos(radAngle),-sin(radAngle),0,0),
		vec4(sin(radAngle),cos(radAngle),0,0),
		vec4(0,0,1,0),
		vec4(0,0,0,1)
	);//*/
	
	counterOrbitMat = mat4(
		vec4(cos(-radAngle),-sin(-radAngle),0,0),
		vec4(sin(-radAngle),cos(-radAngle),0,0),
		vec4(0,0,1,0),
		vec4(0,0,0,1)
	);//*/
	
	radAngle = (mod(orientationState,4)*90.0)*M_PI/180;
	//radAngle = 3.14;
	
	orientationMat = mat4(
		vec4(cos(radAngle),-sin(radAngle),0,0),
		vec4(sin(radAngle),cos(radAngle),0,0),
		vec4(0,0,1,0),
		vec4(0,0,0,1)
	);
	
	scaleMat = mat4(
		vec4(scaleState,0,0,0),
		vec4(0,scaleState,0,0),
		vec4(0,0,scaleState,0),
		vec4(0,0,0,1)
	);
	
	//transformation applications
	gl_Position = (
		gl_Position*orientationMat*counterOrbitMat*orbiting
		+
		gl_Position*orientationMat*mod(orbiting+1,2)
	);
	
	gl_Position = (gl_Position*scaleMat);
	
	gl_Position = (
		(gl_Position)
		+
		vec4(offset,0,0,0)*mod(orbiting+1,2)
		+
		vec4(0.5,0,0,0)*orbiting
	);
	
	gl_Position = (
		gl_Position*mod(orbiting+1,2)
		+
		gl_Position*orbitMat*orbiting
	);
	
	//grad_color = grad_color*orbitMat;
}