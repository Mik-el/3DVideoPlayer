precision mediump float;
varying vec3 vTextureCoord;   // �Ӷ�����ɫ����������
varying vec3 vPosition;//���մӶ�����ɫ�������Ķ���λ��
uniform sampler2D mapTex;  // ��ͼ����

void main()                         
{
	mediump vec2 respos = vTextureCoord.xy;
	gl_FragColor = texture2D(mapTex, respos);
}
