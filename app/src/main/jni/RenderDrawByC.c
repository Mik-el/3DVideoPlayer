#include "RenderDrawByC.h"
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>


float gblWPer = 1; 
float gblHper = 1;


float vertexHalfOrign[] = {
	// X, Y, Z
	-1.0, -1.0, 0,
	1.0, -1.0, 0, 
	-1.0,  1.0, 0,
	1.0,   1.0, 0
};

float vertexHalf[] = {
	// X, Y, Z
	-1.0, -1.0, 0,
	1.0, -1.0, 0, 
	-1.0,  1.0, 0,
	1.0,   1.0, 0
};

float texcoordHalf[] = {
	// U, V
	0, 0,
	0.5, 0,
	0, 1,
	0.5, 1
};

float texcoordHalfR[] = {
	// U, V
	0.5, 0,
	1, 0,
	0.5, 1,
	1, 1
};

float texcoordHalfTop[] = {
	// U, V
	0, 0,
	1, 0,
	0, 0.5,
	1, 0.5
};

float texcoordHalfBottom[] = {
	// U, V
	0, 0.5,
	1, 0.5,
	0, 1,
	1, 1
};

float vetexFullOrign[] = {
	// X, Y, Z
	-1.0, -1.0, 0, 
	1.0, -1.0, 0, 
	-1.0,  1.0, 0,
	1.0,   1.0, 0
};

float vetexFull[] = {
	// X, Y, Z
	-1.0, -1.0, 0, 
	1.0, -1.0, 0, 
	-1.0,  1.0, 0,
	1.0,   1.0, 0
};

float texcoordFull[] = {
	// U, V
	0, 0,
	1, 0,
	0, 1,
	1, 1
};
		

JNIEXPORT jint JNICALL Java_com_estar_Holography_RenderDrawByC_drawRender
  (JNIEnv *env, jclass clazz, jint vertexPos, jint texcoordPos)
{
	glVertexAttribPointer(vertexPos, 3, GL_FLOAT, 0,
			3*sizeof(float), vetexFull);
	glEnableVertexAttribArray(vertexPos);

	glVertexAttribPointer(texcoordPos, 3, GL_FLOAT, 0,
			 2*sizeof(float), texcoordFull);

	glEnableVertexAttribArray(texcoordPos);
}
  

JNIEXPORT jint JNICALL Java_com_estar_Holography_RenderDrawByC_drawRender2D
  (JNIEnv *env, jclass clazz, jint vertexPos, jint texcoordPos)
{
	glVertexAttribPointer(vertexPos, 3, GL_FLOAT, 0,
			3*sizeof(float), vertexHalf);
	glEnableVertexAttribArray(vertexPos);

	glVertexAttribPointer(texcoordPos, 3, GL_FLOAT, 0,
			 2*sizeof(float), texcoordHalf);

	glEnableVertexAttribArray(texcoordPos);
}

JNIEXPORT jint JNICALL Java_com_estar_Holography_RenderDrawByC_drawRender2DTop
  (JNIEnv *env, jclass clazz, jint vertexPos, jint texcoordPos)
{
	glVertexAttribPointer(vertexPos, 3, GL_FLOAT, 0,
			3*sizeof(float), vertexHalf);
	glEnableVertexAttribArray(vertexPos);

	glVertexAttribPointer(texcoordPos, 3, GL_FLOAT, 0,
			 2*sizeof(float), texcoordHalfTop);

	glEnableVertexAttribArray(texcoordPos);
}


JNIEXPORT jint JNICALL Java_com_estar_Holography_RenderDrawByC_drawRender2DBottom
  (JNIEnv *env, jclass clazz, jint vertexPos, jint texcoordPos)
{
	glVertexAttribPointer(vertexPos, 3, GL_FLOAT, 0,
			3*sizeof(float), vertexHalf);
	glEnableVertexAttribArray(vertexPos);

	glVertexAttribPointer(texcoordPos, 3, GL_FLOAT, 0,
			 2*sizeof(float), texcoordHalfBottom);

	glEnableVertexAttribArray(texcoordPos);
}

JNIEXPORT jint JNICALL Java_com_estar_Holography_RenderDrawByC_drawRender2DR
  (JNIEnv *env, jclass clazz, jint vertexPos, jint texcoordPos)
{
	glVertexAttribPointer(vertexPos, 3, GL_FLOAT, 0,
			3*sizeof(float), vertexHalf);
	glEnableVertexAttribArray(vertexPos);

	glVertexAttribPointer(texcoordPos, 3, GL_FLOAT, 0,
			 2*sizeof(float), texcoordHalfR);

	glEnableVertexAttribArray(texcoordPos);
}


jint JNICALL Java_com_estar_Holography_RenderDrawByC_setPercent
  (JNIEnv *env, jclass clazz, jfloat wper, jfloat hper)
{
	int i = 0;
	gblWPer = wper;
	gblHper = hper;
	
	for( i = 0; i < 4; i++ ) {
		vertexHalf[3 * i + 0] = vertexHalfOrign[3 * i + 0] * wper;
		vertexHalf[3 * i + 1] = vertexHalfOrign[3 * i + 1] * hper;
		
		vetexFull[3 * i + 0] = vetexFullOrign[3 * i + 0] * wper;
		vetexFull[3 * i + 1] = vetexFullOrign[3 * i + 1] * hper;
	}
	return 0;
}


