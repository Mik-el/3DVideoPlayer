precision mediump float;
varying vec2 vTextureCoord;//���մӶ�����ɫ�������Ĳ���
uniform sampler2D sTexture;//�����������
uniform float leftpostion;
uniform float width;   // �ܿ��
void main()                         
{           
   //���ƬԪ�������в������ɫֵ  
   mediump vec2 respos = vTextureCoord.xy;
   respos.y = 1.0 -  respos.y; 
   float left = leftpostion / width;
   vec2  tmp =  vec2(left,0);
   float one = 1.0 / width;

   gl_FragColor = (texture2D(sTexture, respos+tmp) + texture2D(sTexture, respos+tmp-one))*0.5;

}              