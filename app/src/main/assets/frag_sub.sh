precision mediump float;
varying vec3 vTextureCoord;   // 从顶点着色器传过来的
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
uniform sampler2D mapTex;  // 贴图纹理

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
