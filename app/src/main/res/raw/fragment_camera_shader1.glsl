#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_texPosition;
uniform samplerExternalOES s_texture;
void main() {
//    gl_FragColor = vec4(vec3(1.0 - texture2D(s_texture, v_texPosition)), 1.0);
    gl_FragColor = texture2D(s_texture, v_texPosition);
}
