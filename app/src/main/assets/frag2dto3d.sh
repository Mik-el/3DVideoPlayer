#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;
uniform float  screenHeight;
uniform float  is2dto3d;
void main() {
	if(is2dto3d > 1.0){
		float p = vTextureCoord.y * screenHeight;
		vec2  tmp = vTextureCoord;
		tmp.x += 0.004 - p*0.0000122;
		gl_FragColor = texture2D(sTexture, tmp);
	}else {
		gl_FragColor = texture2D(sTexture, vTextureCoord);
	}
	
	if(vTextureCoord.t > 0.990740741){
		gl_FragColor = vec4(0.0,0.0,0.0,1.0);
	}
	
	
}