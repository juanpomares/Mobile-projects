package com.opengl10.android.util;

/* Copyright (C) 2014 Juan Antonio Puchol Garcia
 * Depto. Ciencia de la Computacion e Inteligencia Artificial
 * Universidad de Alicante
 * 
 * Master Universitario en Desarrollo de Software para Dispositivos M�viles
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/* Versi�n 2.5 de Resource3DSReader
*  Changelog:
*    Cambios desde la versi�n 2.0:
*    	Lee grupos de suavizado
*    Cambios desde la versi�n 1.5:
*       Lee m�ltiples mallas del recurso 3DS
*    Cambios desde la versi�n 1.0:
*       Se a�ade el c�lculo de la normal y las coordenadas de texturas
*       Cada v�rtice tiene 8 floats (x, y, z, A, B, C, u, v)
*/

public class Resource3DSReader {
	private static final String TAG = "Resource3DSReader";
	
	// Constantes
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
	private static final int TRIMESH_SIZE    = 8*3;
	
	// N�mero m�ximo de mallas de tri�ngulos a leer
	private static final int MAX_MESHES      = 500;
	
	// Identificadores de los trozos (chunks) del archivo que vamos a leer
	// Descripci�n del formato en: http://es.wikipedia.org/wiki/.3ds
	private static final int CHUNK_MAIN		= 0x4d4d;
	private static final int CHUNK_OBJMESH	= 0x3d3d;
	private static final int CHUNK_OBJBLOCK	= 0x4000;
	private static final int CHUNK_TRIMESH	= 0x4100;
	private static final int CHUNK_VERTLIST	= 0x4110;
	private static final int CHUNK_FACELIST	= 0x4120;
	private static final int CHUNK_MAPLIST	= 0x4140;
	private static final int CHUNK_SMOOLIST	= 0x4150;
	
	int numVer[];
	int numPol[];
	int numUv[];
	
	int maxVer;
	int maxPol;
	int maxUv;
		
	// Vectores temporales
	float[] vertexBuffer;
	int[]	polBuffer;
	float[]	uvBuffer;
	int[]	smoothBuffer;
	
	// [Salida] Vectores (JNI) con la mallas de tri�ngulos resultante, y su n�mero de v�rtices 
	public FloatBuffer[]	dataBuffer;
	public int[]			numVertices;	
	
	// N�mero de mallas
	public int numMeshes;
	
	public Resource3DSReader() {
		numMeshes = -1;
		numVer = new int[MAX_MESHES];
		numPol = new int[MAX_MESHES];
		numUv  = new int[MAX_MESHES];
		maxVer = 0;
		maxPol = 0;
		maxUv  = 0;
	}
	
	private void vector_cross(float[] N, float[] va, float[] vb) {
		N[0] = va[1] * vb[2] - va[2] * vb[1];
		N[1] = va[2] * vb[0] - va[0] * vb[2];
		N[2] = va[0] * vb[1] - va[1] * vb[0];
	}

	private void vector_normalize(float[] N) {
		double m = Math.sqrt(N[0]*N[0] + N[1]*N[1]+ N[2]*N[2]);
		if (m>0) {
			N[0] /= m;
			N[1] /= m;
			N[2] /= m;
		}	
	}
	
	private int readUnsignedShort(InputStream is) {
		
		int a, b; 
		
		try {
			a = is.read();
			b = is.read();
		
		} catch (IOException e) {
			throw new RuntimeException("No se pudo abrir el recurso", e);
		} catch (Resources.NotFoundException nfe) {
			throw new RuntimeException("Recurso no encontrado", nfe);
		}
		return a + (b << 8);
	}

	private int readUnsignedInt(InputStream is) {
		
		int a, b, c, d; 
		
		try {
			a = is.read();
			b = is.read();
			c = is.read();
			d = is.read();
			
		} catch (IOException e) {
			throw new RuntimeException("No se pudo abrir el recurso", e);
		} catch (Resources.NotFoundException nfe) {
			throw new RuntimeException("Recurso no encontrado", nfe);
		}
		return a + (b << 8) + (c << 16) + (d <<24);
	}
	
	private float readFloat(InputStream is) {
		return Float.intBitsToFloat(readUnsignedInt(is));
	}
	
	public void analize3DSFromResource(Context context, int resourceId) {
		int 	l_chunk_id;
		int 	l_chunk_length;
		int 	l_byte;
		int		i;
		String 	name;
		int		totalPol;
		
		numMeshes=-1;
		totalPol=0;

		try {
			InputStream inputStream = context.getResources().openRawResource(resourceId);
	
			// Bucle para leer los trozos (chunks) de archivo mientras queden bytes por leer
			while (inputStream.available() > 0) {
				
				// Lee la cabecera, id y longitud								
				l_chunk_id = readUnsignedShort(inputStream);
				l_chunk_length = readUnsignedInt(inputStream);
				//if (LoggerConfig.ON) {
					//Log.w(TAG, "[A] Leyendo 3DS id: " + l_chunk_id);
					//Log.w(TAG, "[A] Leyendo 3DS length: " + l_chunk_length);
				//}		
				switch (l_chunk_id)
			    {
			    	case CHUNK_MAIN: 
			    		break;
			        
			    	case CHUNK_OBJMESH:
			        	break;
			         
			        case CHUNK_OBJBLOCK: 
			        	
			        	name="";
			        	i=0;
			            do {
			            	l_byte = inputStream.read();
			            	if (l_byte>0) name=name+(char)l_byte;
			            	i++;
			            } while(l_byte != 0 && i<20);
			            numMeshes++;
			            if (LoggerConfig.ON) {
			        		Log.w(TAG, "[A] CHUNK_OBJBLOCK [" + numMeshes + "]: " + name);
						}
			            break;
			        
			        case CHUNK_TRIMESH:
			        	break;  
			        	
			        case CHUNK_VERTLIST: 
			        	numVer[numMeshes] = readUnsignedShort(inputStream);
			        	//if (LoggerConfig.ON) {
							//Log.w(TAG, "[A] N�mero de V�rtices: " + numVer[numMeshes]);
						//}
			            inputStream.skip(numVer[numMeshes]*3*BYTES_PER_FLOAT);
			            break;
			            
			        case CHUNK_FACELIST:
			        	numPol[numMeshes] = readUnsignedShort(inputStream);
			        	//if (LoggerConfig.ON) {
							//Log.w(TAG, "[A] N�mero de Pol�gonos: " + numPol[numMeshes]);
						//}
			            inputStream.skip(numPol[numMeshes]*4*BYTES_PER_SHORT);
			            totalPol+=numPol[numMeshes];
			            break;
			            
			        case CHUNK_MAPLIST:
			        	numUv[numMeshes] = readUnsignedShort(inputStream);
			        	//if (LoggerConfig.ON) {
							//Log.w(TAG, "[A] N�mero de Uv: " + numUv[numMeshes]);
						//}
			            inputStream.skip(numUv[numMeshes]*2*BYTES_PER_FLOAT);
			            break;
			            
			        default:
			            //if (LoggerConfig.ON) {
							//Log.w(TAG, "[A] 3DS saltando: " + (l_chunk_length-6) + " bytes, quedan: "+inputStream.available());
						//}
			            inputStream.skip(l_chunk_length-6);
			        	break;
			    }	
			}
			inputStream.close();
		} catch (EOFException e) {
			throw new RuntimeException("Fin de fichero EOF: " + resourceId, e);
		} catch (IOException e) {
			throw new RuntimeException("No se pudo abrir el recurso: " + resourceId, e);
		} catch (Resources.NotFoundException nfe) {
			throw new RuntimeException("Recurso no encontrado: " + resourceId, nfe);
		}
		numMeshes++;		
		
		dataBuffer = new FloatBuffer[numMeshes];
		numVertices = new int[numMeshes];
		
		maxVer=0;
		maxPol=0;
		maxUv=0;
		for (i=0; i<numMeshes; i++) {
			if (maxVer<numVer[i]) maxVer=numVer[i];
			if (maxPol<numPol[i]) maxPol=numPol[i];
			if (maxUv<numUv[i]) maxUv=numUv[i];
			numVertices[i] = numPol[i]*3;
		}
		if (LoggerConfig.ON) {
			if (numMeshes==1)
				Log.w(TAG, "[A] Recurso 3DS analizado correctamente, con " + numMeshes + " malla y " + totalPol + " pol�gonos");
			else
				Log.w(TAG, "[A] Recurso 3DS analizado correctamente, con " + numMeshes + " mallas y " + totalPol + " pol�gonos");
		}
	}

	private void expandVertices(int nM) {
		int i, j, pos;
		
		float[]	va = new float[3];
		float[] vb = new float[3];
		float[] N  = new float[3];
		
		// Crea un buffer en JNI (Java Native Interface)
		dataBuffer[nM] = ByteBuffer
					.allocateDirect(numPol[nM] * TRIMESH_SIZE * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
				
		// Crea la malla de tri�ngulos
		for (i=0; i<numPol[nM]; i++) {
					
			// Para cada v�rtice
			for(j=0;j<3;j++) {
				pos = polBuffer[i*3+j];
						
				// A�adimos (x, y, z)
				dataBuffer[nM].put(i*24+j*8, vertexBuffer[pos*3]);
				dataBuffer[nM].put(i*24+j*8+1, vertexBuffer[pos*3+1]);
				dataBuffer[nM].put(i*24+j*8+2, vertexBuffer[pos*3+2]);
						
				// A�adimos las coordenadas de textura (u,v)
				if (numUv[nM]>0) {
					dataBuffer[nM].put(24*i+j*8+6, uvBuffer[pos*2]);
					dataBuffer[nM].put(24*i+j*8+7, uvBuffer[pos*2+1]);
				} else {
					dataBuffer[nM].put(24*i+j*8+6, 0.0f);
					dataBuffer[nM].put(24*i+j*8+7, 0.0f);
				}
			}
			// Para cada tri�ngulo se calcula la normal N = va x vb
			for (j=0; j<3; j++) {
				va[j] = dataBuffer[nM].get(i*24+8*2+j) - dataBuffer[nM].get(i*24+j);
				vb[j] = dataBuffer[nM].get(i*24+8+j)   - dataBuffer[nM].get(i*24+j);
			}
			vector_cross(N, va, vb);
			vector_normalize(N);
					
			// A�adimos las normal N(A, B, C)
			for(j=0;j<3;j++) {
				dataBuffer[nM].put(i*24+j*8+3, N[0]);
				dataBuffer[nM].put(i*24+j*8+4, N[1]);
				dataBuffer[nM].put(i*24+j*8+5, N[2]);
			}
					
		}
	}
	
	public int read3DSFromResource(Context context, int resourceId) {
		int 	l_chunk_id;
		int 	l_chunk_length;
		int 	l_byte;
		int		i;
		String 	name;
		
		
		analize3DSFromResource(context, resourceId);
		numMeshes=-1;
		
		// Crea los bufferes temporales
		vertexBuffer=new float[maxVer*3];
		polBuffer=new int[maxPol*3];
		uvBuffer=new float[maxUv*2];
		smoothBuffer=new int[maxPol];
		
		if (LoggerConfig.ON) {
			Log.w(TAG, "[R] Leyendo 3DS...");
		}
		try {
			InputStream inputStream = context.getResources().openRawResource(resourceId);
	
			// Bucle para leer los trozos (chuks) de archivo mientras queden bytes por leer
			while (inputStream.available() > 0) {
				
				// Lee la cabecera, id y longitud								
				l_chunk_id = readUnsignedShort(inputStream);
				l_chunk_length = readUnsignedInt(inputStream);
				
				//if (LoggerConfig.ON) {
					//Log.w(TAG, "Leyendo 3DS id: " + l_chunk_id);
					//Log.w(TAG, "Leyendo 3DS length: " + l_chunk_length);
				//}
				switch (l_chunk_id)
			    {
			    	case CHUNK_MAIN: 
			    		break;
			        
			    	case CHUNK_OBJMESH:
			        	break;
			         
			        case CHUNK_OBJBLOCK: 
			        	
			        	if (numMeshes>-1) expandVertices(numMeshes);
			        	name="";
			        	i=0;
			            do {
			            	l_byte = inputStream.read();
			            	if (l_byte>0) name=name+(char)l_byte;
			            	i++;
			            } while(l_byte != 0 && i<20);
			            //if (LoggerConfig.ON) {
			        		Log.w(TAG, "[R] CHUNK_OBJBLOCK: " + name);
						//}
			            numMeshes++;
			            break;
			        
			        case CHUNK_TRIMESH:
			        	break;  
			        	
			        case CHUNK_VERTLIST: 
			        	numVer[numMeshes] = readUnsignedShort(inputStream);
				        	
			            //if (LoggerConfig.ON) {
							//Log.w(TAG, "[R] N�mero de V�rtices: " + numVer[numMeshes]);
						//}
			            for (i=0; i<numVer[numMeshes]; i++)
			            {
			            	vertexBuffer[i*3]   = readFloat(inputStream);
			            	vertexBuffer[i*3+1] = readFloat(inputStream);
			            	vertexBuffer[i*3+2] = readFloat(inputStream);
			            }
			            break;
			            
			        case CHUNK_FACELIST:
			        	numPol[numMeshes] = readUnsignedShort(inputStream);
			        			        	        	
			        	//if (LoggerConfig.ON) {
							//Log.w(TAG, "[R] N�mero de Pol�gonos: " + numPol[numMeshes]);
						//}
			        			        		            
			        	for (i=0; i<numPol[numMeshes]; i++)
			            {
			        		polBuffer[i*3]   = readUnsignedShort(inputStream);
			        		polBuffer[i*3+1] = readUnsignedShort(inputStream);
			        		polBuffer[i*3+2] = readUnsignedShort(inputStream);
			        		l_byte = readUnsignedShort(inputStream);
			            }
			        	//int bytes_left = l_chunk_length - numPol[numMeshes] * 4 * BYTES_PER_SHORT - 2;  
			        	//if (LoggerConfig.ON) {
			        		//Log.w(TAG, "[R] Facelist, quedan: " + bytes_left + " bytes");
			        	//}
			        
			            break;
			            
			        case CHUNK_MAPLIST:
			        	numUv[numMeshes] = readUnsignedShort(inputStream);
			        
			        	if (LoggerConfig.ON) {
							Log.w(TAG, "[R] N�mero de uv's: " + numUv[numMeshes]);
						}
			        				        	
			        	for (i=0; i<numUv[numMeshes]; i++)
			            {
			            	uvBuffer[i*2]   = readFloat(inputStream);
			            	uvBuffer[i*2+1] = 1.0f - readFloat(inputStream);
			            }
			            break;
			         
			        case CHUNK_SMOOLIST:
			        	if (LoggerConfig.ON) {
							Log.w(TAG, "[R] Leyendo smoothgroup... ");
			        	}
			        	for(i = 0; i < numPol[numMeshes]; i++) {
			    			smoothBuffer[i] = readUnsignedInt(inputStream);
			    		}
			        	break;
			        default:
			           	//if (LoggerConfig.ON) {
							//Log.w(TAG, "[R] 3DS saltando: " + (l_chunk_length-6) + " bytes");
						//}
			        	inputStream.skip(l_chunk_length-6);
			        	break;
			    }
			}
			inputStream.close();
		} catch (EOFException e) {
			throw new RuntimeException("Fin de fichero EOF: " + resourceId, e);
		} catch (IOException e) {
			throw new RuntimeException("No se pudo abrir el recurso: " + resourceId, e);
		} catch (Resources.NotFoundException nfe) {
			throw new RuntimeException("Recurso no encontrado: " + resourceId, nfe);
		}
		
		expandVertices(numMeshes);
		numMeshes++;
		
		// Liberamos la memoria (buffers) temporal
		vertexBuffer=null;
		polBuffer=null;
		uvBuffer=null;
		
		if (LoggerConfig.ON) {
			Log.w(TAG, "[R] Recurso 3DS le�do correctamente, con " + numMeshes + " malla(s).");
		}
		return numMeshes;
	}
}

/* Leer grupos de smooth
for(i = 0; i < trimesh->num_face; i++) {
int j = i * 3,k;
int v0 = face[j + 0];
int v1 = face[j + 1];
int v2 = face[j + 2];
for(k = 1; k <= vertex_face[v0][0]; k++) {
	int l = vertex_face[v0][k];
	if(i == l || (smoothgroup && smoothgroup[i] & smoothgroup[l]))
		vector_add(&normal_vertex[j + 0],&normal_face[l],
			&normal_vertex[j + 0]);
}
for(k = 1; k <= vertex_face[v1][0]; k++) {
	int l = vertex_face[v1][k];
	if(i == l || (smoothgroup && smoothgroup[i] & smoothgroup[l]))
		vector_add(&normal_vertex[j + 1],&normal_face[l],
			&normal_vertex[j + 1]);
}
for(k = 1; k <= vertex_face[v2][0]; k++) {
	int l = vertex_face[v2][k];
	if(i == l || (smoothgroup && smoothgroup[i] & smoothgroup[l]))
		vector_add(&normal_vertex[j + 2],&normal_face[l],
			&normal_vertex[j + 2]);
}
}
*/