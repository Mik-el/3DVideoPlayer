attribute vec3 aPosition;  //����λ��
attribute vec2 aTexCoor;    //������������
varying highp vec2 vTextureCoord;  //���ڴ��ݸ�ƬԪ��ɫ���ı���

void main()  
{
	gl_Position = vec4(aPosition, 1.0); //�����ܱ任�������˴λ��ƴ˶���λ��
	vTextureCoord = aTexCoor; //�����յ��������괫�ݸ�ƬԪ��ɫ��
}

