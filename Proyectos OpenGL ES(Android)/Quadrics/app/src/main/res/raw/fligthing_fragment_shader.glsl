precision mediump float;			// Precisión media

uniform sampler2D u_TextureUnit;	// in: Unidad de Textura
varying vec4 v_Color;				// in: color recibido desde el vertex shader
varying vec2 v_UV;					// in: UVs recibidas desde el vertex shader

varying vec3 v_Position;       // Interpolated position for this fragment.
varying vec3 v_Normal;

void main()
{
	float ambient  = 0.15;									// 15% de intensidad ambiente
    vec4  specularColor = vec4(1, 1, 1, 1);					// Color especular (brillos blancos)

	vec4 ColorFinal= v_Color*ambient;

	vec3 LightPos0 = vec3(5, -1, -3);						// Posición de la luz 0 [fija]

	vec3 P = v_Position;					// Posición del vértice
	vec3 N = v_Normal;    					// Normal del vértice

	// Primera Luz
	float d = length(P - LightPos0);						// distancia
	vec3  L = normalize(P - LightPos0);						// Vector Luz
	vec3  V = normalize(P);	  								// Vector Visión (Eye)
	vec3  R = normalize(reflect(-L, N));					// Vector reflejado R=2N(N.L)-L

	float attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 		// Cálculo de la atenuación
	float diffuse  = max(dot(N, L), 0.0);					// Cálculo de la intensidad difusa
	float specular = pow(max(dot(V, R), 0.0), 100.0);		// Exponente de Phong (200)

	ColorFinal+=attenuation*(v_Color*diffuse+ specularColor*specular);
	gl_FragColor = ColorFinal*texture2D(u_TextureUnit, v_UV);
}