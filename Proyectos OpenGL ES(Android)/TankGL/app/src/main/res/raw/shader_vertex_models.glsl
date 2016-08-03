uniform mat4 u_MVPMatrix;   		// in: Projection*ModelView Matrix
uniform mat4 u_MVMatrix;			// in: ModelView Matrix
uniform vec4 u_Color;				// in: Object color
uniform int u_LightsEnabled;		// in: Lights enabled

attribute vec4 a_Position;			// in: Each vertex position
attribute vec3 a_Normal;			// in: Each normal position
attribute vec2 a_UV;				// in: UV coordinates from the texture mapping

varying vec4 v_Color;				// out: Output color passed to the Fragment Shader
varying vec2 v_UV;					// out: Output UVs passed to the Fragment Shader


void main()
{
	if(u_LightsEnabled!=0)
	{
		float ambient  = 0.15;									// 15% ambieent intensity
		vec4  specularColor = vec4(1, 1, 1, 1);					// Specular Color (White shines)

		vec3 LightPos0 = vec3( 2,  5, 3);						// Light 0 position(fixed)
		vec3 LightPos1 = vec3(-4, -5, 3);						// Light 1 position(fixed)

		vec3 P = vec3(u_MVMatrix * a_Position);					// Vertex position
		vec3 N = vec3(u_MVMatrix * vec4(a_Normal, 0.0));    	// Normal position

		// Primera Luz
		float d = length(P - LightPos0);						// Distance
		vec3  L = normalize(P - LightPos0);						// Light Vector
		vec3  V = normalize(P);	  								// Sight Vector(Eye)
		vec3  R = normalize(reflect(-L, N));					// Reflected Vector R=2N(N.L)-L

		float attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 		// Attenuation calculation

		float diffuse  = max(dot(N, L), 0.0);					// Diffuse intensity calculation
		float specular = pow(max(dot(V, R), 0.0), 200.0);		// Phong exponent (200)

		v_Color = u_Color*ambient+attenuation*(u_Color*diffuse + specularColor*specular);

		// Segunda Luz
		d = length(P - LightPos1);								// Distance
		L = normalize(P - LightPos1);							// Light Vector
		V = normalize(P);	  									// Sight Vector(Eye)
		R = normalize(reflect(-L, N));							// Reflected Vector R=2N(N.L)-L

		attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 			// Attenuation calculation

		diffuse  = max(dot(N, L), 0.0);							// Diffuse intensity calculation
		specular = pow(max(dot(V, R), 0.0), 200.0);				// Phong exponent (200)

		v_Color += attenuation*(u_Color*diffuse + specularColor*specular);
	}else
		v_Color=u_Color;

	v_UV = a_UV;
	gl_Position = u_MVPMatrix * a_Position;
}