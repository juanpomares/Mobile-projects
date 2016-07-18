
uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
uniform vec4 u_Color;

varying vec4 v_Color;

void main()
{
	v_Color = u_Color;
	gl_Position = u_MVPMatrix * a_Position;
}