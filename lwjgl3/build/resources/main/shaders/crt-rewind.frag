#version 120

#ifdef GL_ES
precision mediump float;
#endif

// Uniforms
uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;
uniform vec2 u_viewportOffset;


// Parameters
float scanlines_opacity = 0.4;
float scanlines_width = 0.25;
float grille_opacity = 0.3;
bool roll = true;
float roll_speed = 8.0;
float roll_size = 15.0;
float roll_variation = 1.8;
float distort_intensity = 0.05;
float noise_opacity = 0.4;
float noise_speed = 5.0;
float static_noise_intensity = 0.06;
float aberration = 0.03;
float brightness = 1.4;
bool discolor = true;
float vignette_intensity = 0.8;
float vignette_opacity = 0.7;


// Random and Noise functions
vec2 random(vec2 uv) {
    uv = vec2(dot(uv, vec2(127.1, 311.7)), dot(uv, vec2(269.5, 183.3)));
    return -1.0 + 2.0 * fract(sin(uv) * 43758.5453123);
}

float noise(vec2 uv) {
    vec2 uv_index = floor(uv);
    vec2 uv_fract = fract(uv);
    vec2 blur = smoothstep(0.0, 1.0, uv_fract);
    return mix(
        mix(dot(random(uv_index + vec2(0.0, 0.0)), uv_fract - vec2(0.0, 0.0)),
            dot(random(uv_index + vec2(1.0, 0.0)), uv_fract - vec2(1.0, 0.0)), blur.x),
        mix(dot(random(uv_index + vec2(0.0, 1.0)), uv_fract - vec2(0.0, 1.0)),
            dot(random(uv_index + vec2(1.0, 1.0)), uv_fract - vec2(1.0, 1.0)), blur.x), blur.y)
    * 0.5 + 0.5;
}

// Vignette function
float vignette(vec2 uv) {
    uv *= 1.0 - uv.xy;
    float vig = uv.x * uv.y * 15.0;
    return pow(vig, vignette_intensity * vignette_opacity);
}

// Function to desaturate the color (convert to grayscale)
vec3 desaturate(vec3 color) {
    float gray = dot(color, vec3(0.299, 0.587, 0.114)); // Luminance formula
    return vec3(gray);
}

// Function to apply a blue tint
vec3 applyBlueTint(vec3 color) {
    return vec3(color.r, color.g, color.b * 1.3); // Boost the blue channel
}

void main() {
    vec2 uv = (gl_FragCoord.xy - u_viewportOffset) / u_resolution.xy;
    vec2 text_uv = uv;
    vec2 roll_uv = vec2(0.0);

    // Time for animation
    float time = roll ? u_time : 0.0;

    // Rolling effect
    float roll_line = 0.0;
    if (roll || noise_opacity > 0.0) {
        roll_line = smoothstep(0.3, 0.9, sin(uv.y * roll_size - (time * roll_speed)));
        roll_line *= roll_line * smoothstep(0.3, 0.9, sin(uv.y * roll_size * roll_variation - (time * roll_speed * roll_variation)));
        roll_uv = vec2((roll_line * distort_intensity * (1.0 - uv.x)), 0.0);
    }

    vec4 color;
    if (roll) {
        color.r = texture2D(u_texture, text_uv + roll_uv * 0.8 + vec2(aberration, 0.0) * 0.1).r;
        color.g = texture2D(u_texture, text_uv + roll_uv * 1.2 - vec2(aberration, 0.0) * 0.1).g;
        color.b = texture2D(u_texture, text_uv + roll_uv).b;
        color.a = 1.0;
    } else {
        color.r = texture2D(u_texture, text_uv + vec2(aberration, 0.0) * 0.1).r;
        color.g = texture2D(u_texture, text_uv - vec2(aberration, 0.0) * 0.1).g;
        color.b = texture2D(u_texture, text_uv).b;
        color.a = 1.0;
    }

    // Grille
    if (grille_opacity > 0.0) {
        float g_r = smoothstep(0.85, 0.95, abs(sin(uv.x * (u_resolution.x * 3.14159265))));
        color.r = mix(color.r, color.r * g_r, grille_opacity);

        float g_g = smoothstep(0.85, 0.95, abs(sin(1.05 + uv.x * (u_resolution.x * 3.14159265))));
        color.g = mix(color.g, color.g * g_g, grille_opacity);

        float b_b = smoothstep(0.85, 0.95, abs(sin(2.1 + uv.x * (u_resolution.x * 3.14159265))));
        color.b = mix(color.b, color.b * b_b, grille_opacity);
    }

    // Brightness adjustment
    color.rgb = clamp(color.rgb * brightness, 0.0, 1.0);

    // Scanlines
    if (scanlines_opacity > 0.0) {
        float scanlines = smoothstep(scanlines_width, scanlines_width + 0.5, abs(sin(uv.y * (u_resolution.y * 3.14159265))));
        color.rgb = mix(color.rgb, color.rgb * vec3(scanlines), scanlines_opacity);
    }

    // Noise effects
    if (noise_opacity > 0.0) {
        float noise_val = smoothstep(0.4, 0.5, noise(uv * vec2(2.0, 200.0) + vec2(10.0, (time * noise_speed))));
        roll_line *= noise_val * clamp(random(ceil(uv * u_resolution) / u_resolution + vec2(time * 0.8, 0.0)).x + 0.8, 0.0, 1.0);
        color.rgb = clamp(mix(color.rgb, color.rgb + roll_line, noise_opacity), vec3(0.0), vec3(1.0));
    }


    // Apply desaturation and blue tint
    //color.rgb = desaturate(color.rgb);
    float grey = (color.r + color.g + color.b) / 3.0;
    color.rgb = vec3(grey);
    color.rgb = applyBlueTint(color.rgb);

    // Vignette
    color.rgb *= vignette(uv);

    // Static noise
    if (static_noise_intensity > 0.0) {
        color.rgb += clamp(random(ceil(uv * u_resolution) / u_resolution + fract(time)).x, 0.0, 1.0) * static_noise_intensity;
    }

    gl_FragColor = color;
}
