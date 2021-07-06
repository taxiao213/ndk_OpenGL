#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_texPosition;
uniform samplerExternalOES s_texture;
void main() {
    lowp vec4 textureColor = texture2D(s_texture, v_texPosition);
    gl_FragColor = vec4((textureColor.rgb + vec3(-0.5f)), textureColor.w);
}
