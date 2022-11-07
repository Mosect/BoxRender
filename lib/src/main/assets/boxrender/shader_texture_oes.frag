#extension GL_OES_EGL_image_external: require
precision mediump float;
varying vec2 colorCoord;
uniform samplerExternalOES sTexture;

void main() {
    gl_FragColor = texture2D(sTexture, colorCoord);
//    gl_FragColor = vec4(colorCoord.y, 0, 0, 1);
}