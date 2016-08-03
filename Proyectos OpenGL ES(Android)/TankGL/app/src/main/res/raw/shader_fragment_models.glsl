precision mediump float;			// Medium precisionmedia

uniform sampler2D u_TextureUnit;	// in: Texture unit
varying vec4 v_Color;				// in: Received color from the Vertex Shader
varying vec2 v_UV;					// in: Received UVs  rom the Vertex Shader

void main()
{
	vec4 color_final=v_Color*texture2D(u_TextureUnit, v_UV);
	gl_FragColor = vec4(color_final.r, color_final.g, color_final.b, 0.5f);
}