#version 430
out vec4 color;
in vec4 grad_color;

uniform int colorCondition;

void main(void)
{
	if(colorCondition == 1){
		color = vec4(0.0, 0.0, 1.0, 1.0);
	}
	else{
		color = grad_color;
	}
}