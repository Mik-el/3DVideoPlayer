attribute vec3 aPosition;  //����λ��
attribute vec2 aTexCoor;    //������������
varying vec3 vTextureCoord;  //���ڴ��ݸ�ƬԪ��ɫ���ı���

void main()  
{
	gl_Position = vec4(aPosition, 1.0); //�����ܱ任�������˴λ��ƴ˶���λ��
	gl_Position.y = -gl_Position.y;
	vTextureCoord = vec3(aTexCoor, 1.0); //�����յ��������괫�ݸ�ƬԪ��ɫ��
}

