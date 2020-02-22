precision mediump float;
varying vec3 vTextureCoord;   // 从顶点着色器传过来的
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
uniform sampler2D mapTex;  // 贴图纹理

uniform float alpha;
uniform int isdrawtex;
uniform float bianlen;

void main()                         
{
	vec4 finalColor;
	vec4 fcolor;
	
	mediump vec2 respos = vTextureCoord.xy;
	respos.x = respos.x * 1.25 - 0.12;
	
	if( isdrawtex == 1 ) {
   finalColor = texture2D(mapTex, respos);
	}
	else {
		finalColor = vec4(0.4, 0.45, 0.5, 0.3);
	}
   
   if( vTextureCoord.x < bianlen || vTextureCoord.y < bianlen || vTextureCoord.x > (1.0 - bianlen) || vTextureCoord.y > (1.0 - bianlen) )
	{
		finalColor = vec4(0.8, 0.9, 1.0, 0.6);
	}
	finalColor.rgb = finalColor.rgb * alpha;
	if( finalColor.a < 0.4 )
		finalColor = vec4(0.4, 0.45, 0.5, 0.3);
   
   gl_FragColor = finalColor;
   
}
