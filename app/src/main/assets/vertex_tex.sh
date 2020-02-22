uniform mat4 uMVPMatrix; //�ܱ任����
attribute vec4 aPosition;  //����λ��
attribute vec4  aTexCoor;    //�����������
varying vec2 vTextureCoord;  //���ڴ��ݸ�ƬԪ��ɫ���ı���
void main()     
{                            		
   gl_Position = aPosition; //����ܱ任�������˴λ��ƴ˶���λ��
   vTextureCoord = aTexCoor.xy;//�����յ�������괫�ݸ�ƬԪ��ɫ��
}