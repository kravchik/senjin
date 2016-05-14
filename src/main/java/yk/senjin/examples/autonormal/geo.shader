#version 150

//uniform mat3 normalMatrix;

layout(triangles) in;
layout (triangle_strip, max_vertices=3) out;

in vec3 csPosG_fi[3];
in vec4 colorG_fi[3];
in float shininessG_fi[3];

out vec3 csPos_fi;
out vec3 csNormal_fi;
out vec4 color_fi;
out float shininess_fi;

void main()
{
//  vec3 n = normalize(cross(csPosG_fi[2].xyz-csPosG_fi[0].xyz, csPosG_fi[1].xyz-csPosG_fi[0].xyz));
  vec3 n = normalize(cross(csPosG_fi[1].xyz-csPosG_fi[0].xyz, csPosG_fi[2].xyz-csPosG_fi[0].xyz));
//  n = normalMatrix * n;
  for(int i = 0; i < gl_in.length(); i++)
  {
    gl_Position = gl_in[i].gl_Position;
    csPos_fi = csPosG_fi[i];
    csNormal_fi = n;
    color_fi = colorG_fi[i];
    shininess_fi = shininessG_fi[i];
    EmitVertex();
  }
}