#version 120

#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_imageSize;
uniform int u_glowRadius;

void main()
{
    vec4 color = texture2D(u_texture, v_texCoords);
    vec2 pixelToTextureCoords = 1 / u_imageSize;
    vec4 averageColor = vec4(0.0, 0.0, 0.0, 0.0);
    for (int dx = -u_glowRadius; dx <= u_glowRadius; dx++)
    {
        for (int dy = -u_glowRadius; dy <= u_glowRadius; dy++)
        {
            vec2 point = v_texCoords + vec2(dx,dy) * pixelToTextureCoords;
            averageColor += texture2D(u_texture, point);
        }
    }
    averageColor /= pow(2.0 * u_glowRadius + 1.0, 2.0);
    float amount = (sin(6.0 * u_time) + 1.0) * 0.5;
    // extra factor of 2.0 intensifies glow effect
    vec4 glowFactor = vec4( 2.0 * averageColor.rgb, averageColor.a );
    gl_FragColor = v_color * (color + amount * glowFactor);
}



/*
uniform sampler2D u_texture;
const vec4 u_outlineColor = vec4(1, 0.855, 0, 1);

varying vec4 v_color;
varying vec2 v_texCoords;

const float smoothing = 1.0/16.0;
const float outlineWidth = 3.0/16.0;
const float outerEdgeCenter = 0.5 - outlineWidth;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    float distance = texColor.a;  // Assuming alpha channel contains distance

    // Calculate alpha for the inner and outer edges
    float alpha = smoothstep(outerEdgeCenter - smoothing, outerEdgeCenter + smoothing, distance);
    float border = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);

    // Blend outline and texture colors
    vec3 blendedColor = mix(u_outlineColor.rgb, v_color.rgb, border);
    gl_FragColor = vec4(blendedColor, alpha * v_color.a);  // Scale alpha by vertex color alpha
}
/*
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 u_imageSize;
vec4 u_borderColor = vec4(1, 0.855, 0, 1);
float u_borderSize = 3;

void main()
{
    vec4 color = texture2D(u_texture, v_texCoords);
    vec2 pixelToTextureCoords = 1 / u_imageSize;

    bool isInteriorPoint = true;
    bool isExteriorPoint = true;
    for (float dx = -u_borderSize; dx < u_borderSize; dx++)
    {
        for (float dy = -u_borderSize; dy < u_borderSize; dy++)
        {
            vec2 point = v_texCoords + vec2(dx,dy) * pixelToTextureCoords;
            float alpha = texture2D(u_texture, point).a;
            if ( alpha < 0.5 )
                isInteriorPoint = false;
            if ( alpha > 0.5 )
                isExteriorPoint = false;
        }
    }
    if (!isInteriorPoint && !isExteriorPoint && color.a < 0.5)
        gl_FragColor = u_borderColor;
    else
        gl_FragColor = v_color * color;
}
*/
