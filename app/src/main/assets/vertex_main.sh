uniform mat4 uMVPMatrix; //�ܱ任����
attribute vec3 aPosition;  //����λ��
attribute vec2 aTexCoor;    //������������
varying vec3 vPosition;//���ڴ��ݸ�ƬԪ��ɫ���Ķ���λ��
varying vec3 vTextureCoord;  //���ڴ��ݸ�ƬԪ��ɫ���ı���

void main()     
{       
	 //�����ܱ任�������˴λ��ƴ˶���λ��         		
	gl_Position = uMVPMatrix * vec4(aPosition,1.0); 
	vTextureCoord = vec3(aTexCoor, 1.0);
    vPosition = aPosition;
}
