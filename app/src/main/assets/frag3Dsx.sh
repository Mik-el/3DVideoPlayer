precision mediump float;
varying highp vec2 vTextureCoord;
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

void main()
{

	vec2 coord2 = vTextureCoord;
	coord2.t = coord2.t*0.5;
	vec4 coll = texture2D(Sampler0,coord2);
	
	coord2.t += 0.5;
	vec4 colr = texture2D(Sampler0,coord2);
	
	float dis_test = texture2D(Sampler1,vTextureCoord).r;
	
	vec4 rgb = (1.0-dis_test)*coll + dis_test*colr;

	bool sup = fract(dis_test) != 0.0;
	
	if(sup){	
	//	rgb = rgb*(value3-value4*abs(coll-colr));
	//	rgb = rgb*0.0;
		rgb = rgb*(abs(dis_test-0.5)+0.5);
	}
	
  if(vTextureCoord.r > 0.996875) {
  	rgb = vec4(0.0, 0.0, 0.0, 1.0);
  } 

	gl_FragColor = rgb;
}




