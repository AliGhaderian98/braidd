#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 projTrans;
uniform vec2 u_resolution;


void main() {
    // Sample the color from the texture
    vec3 color = texture2D(u_texture, v_texCoords).rgb;

    // Convert to grayscale
    float gray = (color.r + color.g + color.b) / 3.0;
    vec3 greyscale = vec3(gray);

    // Calculate the horizontal distance from the center column of the screen
    float distFromCenter = abs(v_texCoords.x - 0.5);

    // Fade effect: as you move towards the center vertical column, increase the effect of black
    // Adjust the smoothstep thresholds to control the fade range and sharpness
    float fade = smoothstep(0.1, 0.7, distFromCenter);

    // Apply the grayscale color, but blend towards black in the center vertical column
    gl_FragColor = vec4(mix(greyscale, vec3(0.0), 1.0 - fade), 1.0);
}
