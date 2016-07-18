precision mediump float;			// Precisi�n media

uniform sampler2D u_TextureUnit;	// in: Unidad de Textura
varying vec4 v_Color;				// in: color recibido desde el vertex shader
varying vec2 v_UV;					// in: UVs recibidas desde el vertex shader

void main()
{
	vec4 color_final=v_Color*texture2D(u_TextureUnit, v_UV);
	gl_FragColor = vec4(color_final.r, color_final.g, color_final.b, 0.5f);
}