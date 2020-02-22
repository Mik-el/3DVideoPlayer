precision mediump float;
varying vec3 vTextureCoord;   // 从顶点着色器传过来的
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置

uniform vec3 cColor;
uniform vec3 eColor;

void main()                         
{
	mediump vec2 respos = vTextureCoord.xy;
	float dis = (vTextureCoord.x - 0.5) * (vTextureCoord.x - 0.5) + (vTextureCoord.y - 0.5) * (vTextureCoord.y - 0.5);
	dis = 8.0 * dis;
	if( dis < 0.0 ) dis = 0.0; 
	vec3 res = cColor - (cColor - eColor) * dis;
	float a = 0.9;
	vec4 finalColor = vec4(res, a); 
	
	gl_FragColor = finalColor;
}
