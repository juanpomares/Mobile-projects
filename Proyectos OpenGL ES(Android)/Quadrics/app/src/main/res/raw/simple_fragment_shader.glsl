precision mediump float;			// Precisión media

uniform sampler2D u_TextureUnit;	// in: Unidad de Textura
varying vec4 v_Color;				// in: color recibido desde el vertex shader
varying vec2 v_UV;					// in: UVs recibidas desde el vertex shader

void main()
{
	gl_FragColor = v_Color*texture2D(u_TextureUnit, v_UV);
}


/*
precision mediump float;			// Precisión media

varying vec4 v_Color;				// in: color recibido desde el vertex shader

void main()
{
	gl_FragColor = v_Color;
}*/

/*precision mediump float;

varying vec4 v_Color;

void main()
{
	gl_FragColor = v_Color;
}*/

/*
precision mediump float;			// Precisión media

uniform sampler2D u_TextureUnit;	// in: Unidad de Textura
varying vec4 v_Color;				// in: color recibido desde el vertex shader
varying vec2 v_UV;					// in: UVs recibidas desde el vertex shader

void main()
{
	gl_FragColor = v_Color*texture2D(u_TextureUnit, v_UV);
}
*/