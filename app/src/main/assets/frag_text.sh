precision mediump float;
varying vec3 vTextureCoord;   // �Ӷ�����ɫ����������
varying vec3 vPosition;//���մӶ�����ɫ�������Ķ���λ��
uniform sampler2D mapTex;  // ��ͼ����

#define tex2D texture2D

uniform int leftright;

void main()                         
{
	mediump vec2 respos = vTextureCoord.xy;
	respos.x = respos.x * 1.25 - 0.12;
	
	if( leftright == 1 )
		respos.x = respos.x + 0.0039;
	if( leftright == 2 )
		respos.x = respos.x - 0.0039;

   vec4 vColor = tex2D(mapTex, respos);	
   gl_FragColor = vColor;
   
}
