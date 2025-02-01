#version 120

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec2 u_textureSize;
uniform float u_time;
const vec3 u_tintColor = vec3(0.381, 0.969, 0.375);
const float u_blurAmount = 0.04;
const float u_threshold = 0.5;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec4 pixel = texture2D(u_texture, v_texCoords);

    float factor = 0.5 + 0.5 * sin(u_time * 5.0);
    vec3 blendedColor = mix(v_color.rgb, u_tintColor, factor);

    if (pixel.a > u_threshold) {
        gl_FragColor = vec4(pixel.rgb * blendedColor, pixel.a);
    } else {
        float sum = 0.0;
        vec4 blurredColor = vec4(0.0);

        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                vec2 offset = vec2(float(x), float(y)) * (u_blurAmount / u_textureSize);
                blurredColor += texture2D(u_texture, v_texCoords + offset);
            }
        }

        blurredColor /= 9.0;
        gl_FragColor = mix(vec4(pixel.rgb * blendedColor, pixel.a), blurredColor, 0.5);
    }
}
