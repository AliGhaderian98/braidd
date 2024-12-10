#version 120

#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 projTrans;


void main() {
    vec2 uv = v_texCoords;         // Texture coordinates
    vec4 c = texture2D(u_texture, uv);

    // Accumulate neighboring texture samples for blur effect
    c += texture2D(u_texture, uv + 0.001);
    c += texture2D(u_texture, uv + 0.003);
    c += texture2D(u_texture, uv + 0.005);
    c += texture2D(u_texture, uv + 0.007);
    c += texture2D(u_texture, uv + 0.009);
    c += texture2D(u_texture, uv + 0.011);

    c += texture2D(u_texture, uv - 0.001);
    c += texture2D(u_texture, uv - 0.003);
    c += texture2D(u_texture, uv - 0.005);
    c += texture2D(u_texture, uv - 0.007);
    c += texture2D(u_texture, uv - 0.009);
    c += texture2D(u_texture, uv - 0.011);

    // Calculate grayscale value (luminance) from RGB channels
    float grayscale = dot(c.rgb, vec3(0.299, 0.587, 0.114));

    // Desaturate: interpolate between original color and grayscale value
    c.rgb = mix(c.rgb, vec3(grayscale), 0.5);

    // Normalize accumulated color
    c = c / 9.5;

    gl_FragColor = c;
}
