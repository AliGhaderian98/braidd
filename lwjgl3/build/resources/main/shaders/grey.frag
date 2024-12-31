#version 120

#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 projTrans;
uniform vec2 u_resolution;


void main() {
    vec3 color = texture2D(u_texture, v_texCoords).rgb;

    // Convert to grayscale
    float gray = (color.r + color.g + color.b) / 3.0;
    vec3 greyscale = vec3(gray);

    // Make center column black
    float distFromCenter = abs(v_texCoords.x - 0.5);
    float fade = smoothstep(0.1, 0.7, distFromCenter);

    gl_FragColor = vec4(mix(greyscale, vec3(0.0), 1.0 - fade), 1.0);
}
