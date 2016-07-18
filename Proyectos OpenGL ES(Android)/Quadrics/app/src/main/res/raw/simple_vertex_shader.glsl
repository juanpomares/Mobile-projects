uniform mat4 u_MVPMatrix;   		// in: Matriz Projection*ModelView
uniform mat4 u_MVMatrix;			// in: Matriz ModelView
uniform vec4 u_Color;				// in: color del objeto


attribute vec4 a_Position;			// in: Posición de cada vértice
attribute vec3 a_Normal;			// in: Normal de cada vértice
attribute vec2 a_UV;				// in: Coordenadas UV de mapeado de textura

varying vec4 v_Color;				// out: Color de salida al fragment shader
varying vec2 v_UV;					// out: UVs de salida al fragment shader


void main()
{
	float ambient  = 0.15;									// 15% de intensidad ambiente
	vec4  specularColor = vec4(1, 1, 1, 1);					// Color especular (brillos blancos)

	v_Color = u_Color*ambient;

	vec3 LightPos0 = vec3(5, -1, -3);						// Posición de la luz 0 [fija]

	vec3 P = vec3(u_MVMatrix * a_Position);					// Posición del vértice
	vec3 N = vec3(u_MVMatrix * vec4(a_Normal, 0.0));    	// Normal del vértice

	// Primera Luz
	float d = length(P - LightPos0);						// distancia
	vec3  L = normalize(P - LightPos0);						// Vector Luz
	vec3  V = normalize(P);	  								// Vector Visión (Eye)
	vec3  R = normalize(reflect(-L, N));					// Vector reflejado R=2N(N.L)-L

	float attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 		// Cálculo de la atenuación
	float diffuse  = max(dot(N, L), 0.0);					// Cálculo de la intensidad difusa
	float specular = pow(max(dot(V, R), 0.0), 100.0);		// Exponente de Phong (200)

	v_Color += attenuation*(u_Color*diffuse + specularColor*specular);
	gl_Position = u_MVPMatrix * a_Position;
	v_UV = a_UV;
}

	// Segunda Luz
	/*
	vec3 LightPos1 = vec3(-5, 1, 2);						// Posición de la luz 1 [fija]
	d = length(P - LightPos1);								// distancia
	L = normalize(P - LightPos1);							// Vector Luz
	V = normalize(P);	  									// Vector Visión (Eye)
	R = normalize(reflect(-L, N));							// Vector reflejado R=2N(N.L)-L

	attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 			// Cálculo de la atenuación

	diffuse  = max(dot(N, L), 0.0);							// Cálculo de la intensidad difusa
	specular = pow(max(dot(V, R), 0.0), 200.0);				// Exponente de Phong (200)

	v_Color += attenuation*(u_Color*diffuse + specularColor*specular);*/