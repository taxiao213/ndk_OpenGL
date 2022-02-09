uniform mat4 u_Matrix;
uniform float u_Time;

attribute vec3 a_Position;
attribute vec3 a_Color;
attribute vec3 a_DirectionVector;
attribute float a_ParticleStartTime;

varying vec3 v_Color;
varying float v_ElapsedTime;

void main() {
    v_Color=a_Color;
    v_ElapsedTime=u_Time-a_ParticleStartTime;
    // 计算粒子的当前位置，方向向量与运行时间相乘，并于a_Position 相加，运行时间越长，粒子走的越远
    vec3 currentPosition=a_Position+(a_DirectionVector*v_ElapsedTime);
    gl_Position=u_Matrix*vec4(currentPosition, 1.0);
    gl_PointSize =25.0;

}
