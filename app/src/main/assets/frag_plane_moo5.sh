precision mediump float;
varying vec3 vTextureCoord;   // �Ӷ�����ɫ����������
varying vec3 vPosition;//���մӶ�����ɫ�������Ķ���λ��
uniform sampler2D mapTex;  // ��ͼ����

uniform sampler2D hTexBlend;

void main()                         
{
	//���ƬԪ�������в������ɫֵ    
	mediump vec2 respos = vTextureCoord.xy;
	vec4 res;
	vec4 texpos = texture2D(hTexBlend, respos);
	float x = gl_FragCoord.x;
	if( floor(mod(x, 2.0)) == 0.0 ) {
		respos.x = respos.x / 2.0;
		res = texture2D(mapTex, respos);
	}
	else {
		respos.x = respos.x / 2.0 + 0.5;
		res = texture2D(mapTex, respos);
	}
   gl_FragColor = res; 
}
