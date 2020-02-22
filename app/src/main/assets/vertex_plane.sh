attribute vec3 aPosition;  //顶点位置
attribute vec2 aTexCoor;    //顶点纹理坐标
varying highp vec2 vTextureCoord;  //用于传递给片元着色器的变量

void main()  
{
	gl_Position = vec4(aPosition, 1.0); //根据总变换矩阵计算此次绘制此顶点位置
	vTextureCoord = aTexCoor; //将接收的纹理坐标传递给片元着色器
}

