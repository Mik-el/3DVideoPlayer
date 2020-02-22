precision mediump float;
varying highp vec2 vTextureCoord;
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
float value = 30.0/255.0;
float value2 = 0.5;
float value3 = 0.9;
void main()
{
	

	
	vec2 coord2 = vTextureCoord;
	coord2.s = coord2.s*0.5;
	vec4 coll = texture2D(Sampler0,coord2);
	
	coord2.s += 0.5;
	vec4 colr = texture2D(Sampler0,coord2);
	
	float dis_test = texture2D(Sampler1,vTextureCoord).r;
	
	vec4 rgb = (1.0-dis_test)*coll + dis_test*colr;
	
//	bool br = abs(colr.r - coll.r) > value;
//	bool bg = abs(colr.g - coll.g) > value;
//	bool bb = abs(colr.b - coll.b) > value;
	
	/*
	
	bool br = abs(rgb.r - coll.r) > value || abs(rgb.r-colr.r) > value;
	bool bg = abs(rgb.g - coll.g) > value || abs(rgb.g-colr.g) > value;
	bool bb = abs(rgb.b - coll.b) > value || abs(rgb.b-colr.b) > value;
	
	
	
	bool sup = fract(dis_test) != 0.0;

	if( br  &&  sup){
		rgb.r *= value2;
	}
	if( !br  &&  sup){
		rgb.r *= value3;
	}
	
	if( bg  &&  sup ){
		rgb.g *= value2;
	}
	
	if( !bg  &&  sup ){
		rgb.g *= value3;
	}

	if(  bb  &&  sup){
		rgb.b *= value2;
	}
	if( !bb  &&  sup){
		rgb.b *= value3;
	}
*/
	gl_FragColor = rgb;
}




