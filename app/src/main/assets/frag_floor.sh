precision mediump float;
varying vec3 vTextureCoord;   // 从顶点着色器传过来的
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
uniform sampler2D mapTex;  // 贴图纹理

void main()                         
{
	mediump vec2 respos = vTextureCoord.xy;
	gl_FragColor = texture2D(mapTex, respos);
}
