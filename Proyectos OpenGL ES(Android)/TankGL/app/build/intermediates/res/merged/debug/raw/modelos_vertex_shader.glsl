uniform mat4 u_MVPMatrix;   		// in: Matriz Projection*ModelView
uniform mat4 u_MVMatrix;			// in: Matriz ModelView
uniform vec4 u_Color;				// in: color del objeto
uniform int u_LightsEnabled;		// in: luces enchufadas

attribute vec4 a_Position;			// in: Posici�n de cada v�rtice
attribute vec3 a_Normal;			// in: Normal de cada v�rtice
attribute vec2 a_UV;				// in: Coordenadas UV de mapeado de textura

varying vec4 v_Color;				// out: Color de salida al fragment shader
varying vec2 v_UV;					// out: UVs de salida al fragment shader


void main()
{
	if(u_LightsEnabled!=0)
	{
		float ambient  = 0.15;									// 15% de intensidad ambiente
		vec4  specularColor = vec4(1, 1, 1, 1);					// Color especular (brillos blancos)

		vec3 LightPos0 = vec3( 2,  5, 3);						// Posici�n de la luz 0 [fija]
		vec3 LightPos1 = vec3(-4, -5, 3);						// Posici�n de la luz 1 [fija]

		vec3 P = vec3(u_MVMatrix * a_Position);					// Posici�n del v�rtice
		vec3 N = vec3(u_MVMatrix * vec4(a_Normal, 0.0));    	// Normal del v�rtice

		// Primera Luz
		float d = length(P - LightPos0);						// distancia
		vec3  L = normalize(P - LightPos0);						// Vector Luz
		vec3  V = normalize(P);	  								// Vector Visi�n (Eye)
		vec3  R = normalize(reflect(-L, N));					// Vector reflejado R=2N(N.L)-L

		float attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 		// C�lculo de la atenuaci�n

		float diffuse  = max(dot(N, L), 0.0);					// C�lculo de la intensidad difusa
		float specular = pow(max(dot(V, R), 0.0), 200.0);		// Exponente de Phong (200)

		v_Color = u_Color*ambient+attenuation*(u_Color*diffuse + specularColor*specular);

		// Segunda Luz
		d = length(P - LightPos1);								// distancia
		L = normalize(P - LightPos1);							// Vector Luz
		V = normalize(P);	  									// Vector Visi�n (Eye)
		R = normalize(reflect(-L, N));							// Vector reflejado R=2N(N.L)-L

		attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 			// C�lculo de la atenuaci�n

		diffuse  = max(dot(N, L), 0.0);							// C�lculo de la intensidad difusa
		specular = pow(max(dot(V, R), 0.0), 200.0);				// Exponente de Phong (200)

		v_Color += attenuation*(u_Color*diffuse + specularColor*specular);
	}else
		v_Color=u_Color;

	v_UV = a_UV;
	gl_Position = u_MVPMatrix * a_Position;
}