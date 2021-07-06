precision mediump float;
varying vec2 v_texPosition;
uniform sampler2D s_texture;
void main() {
    lowp vec4 textureColor = texture2D(s_texture, v_texPosition);
    float gray = textureColor.r * 0.2125 + textureColor.g * 0.7154 + textureColor.b * 0.0721;
    gl_FragColor = vec4(gray, gray, gray, textureColor.w);
}
