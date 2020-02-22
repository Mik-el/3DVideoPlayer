uniform mat4 uMVPMatrix; //总变换矩阵
attribute vec3 aPosition;  //顶点位置
attribute vec2 aTexCoor;    //顶点纹理坐标
varying vec3 vPosition;//用于传递给片元着色器的顶点位置
varying vec3 vTextureCoord;  //用于传递给片元着色器的变量

void main()     
{       
	 //根据总变换矩阵计算此次绘制此顶点位置         		
	gl_Position = uMVPMatrix * vec4(aPosition,1.0); 
	vTextureCoord = vec3(aTexCoor, 1.0);
    vPosition = aPosition;
}
