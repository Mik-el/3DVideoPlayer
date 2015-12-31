precision mediump float;
varying vec3 vTextureCoord;   // �Ӷ�����ɫ����������
varying vec3 vPosition;//���մӶ�����ɫ�������Ķ���λ��
uniform sampler2D mapTex;  // ��ͼ����

uniform float alpha;

void main()                         
{
	vec2 respos = vTextureCoord.xy;
	vec4 finalColor = texture2D(mapTex, respos);
	finalColor = vec4(finalColor.rgb, 0.6);
//	vec4 finalColor = vec4(1.0, 1.0, 1.0, 0.7);  
//	finalColor.rgb = finalColor.rgb * alpha;

	gl_FragColor = finalColor;
}
