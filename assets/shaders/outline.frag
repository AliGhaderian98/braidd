#version 140

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

in vec2 v_texCoords;
out vec4 fragColor;

void main() {
    // Sample the texture at the given coordinates
    vec4 pixel = texture(u_texture, v_texCoords);

    float factor = 0.5 + 0.5 * sin(u_time*5);  // Oscillates between 0 and 1
    vec3 blendedColor = mix(v_color.rgb, u_tintColor, factor);

    // If the pixel is not fully transparent (inside the sprite), apply the tint color
    if (pixel.a > u_threshold) {
        // Tint the color by multiplying the original pixel color by the interpolated color
        fragColor = vec4(pixel.rgb * blendedColor, pixel.a);
    } else {
        // If the pixel is transparent, sample surrounding pixels to blur the edge
        float sum = 0.0;
        vec4 blurredColor = vec4(0.0);

        // Sample surrounding pixels in a 3x3 grid (can be expanded for more blur)
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                // Apply offset based on the texture size and blur amount
                vec2 offset = vec2(float(x), float(y)) * u_blurAmount / u_textureSize;
                blurredColor += texture(u_texture, v_texCoords + offset);
            }
        }

        // Average the sampled pixels for blur effect
        blurredColor /= 9.0;
        // Combine the blurred color with the original sprite's color (fade the transparency)
        fragColor = mix(vec4(pixel.rgb * blendedColor, pixel.a), blurredColor, 0.5);
    }
}
