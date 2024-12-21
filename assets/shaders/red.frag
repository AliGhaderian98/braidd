#version 130

varying vec4 v_color;
varying vec2 v_texCoords;
uniform mat4 projTrans;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec2 u_viewportOffset;

float vignette_intensity = 0.8;
float vignette_opacity = 0.7;


// Vignette function
float vignette(vec2 uv) {
    uv *= 1.0 - uv.xy;
    float vig = uv.x * uv.y * 15.0;
    return pow(vig, vignette_intensity * vignette_opacity);
}


void main() {
    vec3 color = texture2D(u_texture, v_texCoords).rgb;
    vec2 uv = (gl_FragCoord.xy - u_viewportOffset) / u_resolution.xy;

    // Convert to grayscale
    float gray = (color.r + color.g + color.b) / 3.0;
    vec3 greyscale = vec3(gray);

    // Apply a red tint
    vec3 redTint = vec3(0.7, 0.0, 0.0);
    vec3 tintedColor = mix(color, redTint, 0.45);

    // Calculate vignette effect
    vec2 position = (v_texCoords - vec2(0.5)) * 2;
    float distance = length(position);
    //float vignette = smoothstep(0.8, 0.4, distance);

    // Combine color with vignette
    //vec3 finalColor = mix(tintedColor, vec3(0.0), 1.0 - vignette);

    // Vignette
    tintedColor.rgb *= vignette(uv);

    gl_FragColor = vec4(tintedColor, 1.0);
}
