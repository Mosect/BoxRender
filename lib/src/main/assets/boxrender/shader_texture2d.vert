uniform mat4 cameraMatrix;
uniform mat4 textureMatrix;
attribute vec4 position;
attribute vec4 textureCoord;
varying vec2 colorCoord;

void main() {
    gl_Position = cameraMatrix * position;
    colorCoord = (textureMatrix * textureCoord).xy;
    //    vec4 test = vec4(0.4, 0.4, 0, 1);
    //    colorCoord = (test * textureMatrix).xy;
}
