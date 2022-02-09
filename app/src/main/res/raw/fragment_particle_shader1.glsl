precision mediump float;
varying vec3 v_Color;
varying float v_ElapsedTime;
uniform sampler2D u_TextureUnit;
void main() {
    // 颜色除以运行时间，这个着色器会使年轻的粒子明亮，年老的粒子暗淡，如果除以0，根据规范会导致一个不明确的结果，
    // 但不会导致着色器程序终止,可以在分母上加一个很小的数
    // 使用gl_PointCoord 作为纹理坐标在每个点上绘制一个纹理，纹理的颜色会与点的颜色相乘
    gl_FragColor=vec4(v_Color/v_ElapsedTime, 1.0)*texture2D(u_TextureUnit, gl_PointCoord);

}
