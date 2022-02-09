precision mediump float;
varying vec3 v_Color;
varying float v_ElapsedTime;

void main() {
    // 颜色除以运行时间，这个着色器会使年轻的粒子明亮，年老的粒子暗淡，如果除以0，根据规范会导致一个不明确的结果，
    // 但不会导致着色器程序终止,可以在分母上加一个很小的数
    // 将点绘制成圆
    float xDistance=0.5-gl_PointCoord.x;
    float yDistance=0.5-gl_PointCoord.y;
    float distanceFromCenter = sqrt(xDistance*xDistance + yDistance*yDistance);
    if (distanceFromCenter>0.5){
        // 丢失这个片段，不绘制
        discard;
    } else {
        gl_FragColor=vec4(v_Color/v_ElapsedTime, 1.0);
    }
}
