import sys
import argparse
from PIL import Image

def is_background(r, g, b):
    # Check for pure green screen #00FF00 (with tolerance)
    if g > 150 and r < 100 and b < 100:
        return True
    return False

def process_texture(input_path, output_path, is_glass, keep_bg):
    img = Image.open(input_path).convert("RGBA")
    width, height = img.size
    
    pixels = img.load()
    
    # Optional: We can sample the border to find background colors if keep_bg is False
    bg_colors = []
    if not keep_bg:
        for x in range(width):
            bg_colors.append(pixels[x, 0])
            bg_colors.append(pixels[x, height-1])
        for y in range(height):
            bg_colors.append(pixels[0, y])
            bg_colors.append(pixels[width-1, y])
            
    # Process high-res image first to get crisp edges before downscaling
    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            
            # Remove background
            if not keep_bg:
                if is_background(r, g, b):
                    pixels[x, y] = (0, 0, 0, 0)
                    continue
                
                # Check against border colors for subtle checkerboards
                # AI checkerboard is tricky, let's stick to the green/white heuristic
                
            # Process Glass
            if is_glass and a > 0:
                # If it's a bluish/whitish pixel, make it semi-transparent
                if b > 150 and g > 100 and r < 200:
                    pixels[x, y] = (r, g, b, 150)
                    
    # Now Resize to 16x16 using NEAREST to maintain pixel art style
    img = img.resize((16, 16), resample=Image.NEAREST)
    
    # If the image was originally true pixel art but upscaled to 1024x1024,
    # NEAREST is perfect. However, if the AI generated smooth anti-aliased edges,
    # NEAREST might pick up some semi-transparent edge pixels.
    # To enforce "Minecraft Style", we can threshold the alpha.
    pixels_16 = img.load()
    for y in range(16):
        for x in range(16):
            r, g, b, a = pixels_16[x, y]
            if a < 128 and not is_glass:
                pixels_16[x, y] = (0, 0, 0, 0) # Hard edge
            elif a >= 128 and not is_glass:
                pixels_16[x, y] = (r, g, b, 255) # Hard edge
                
    img.save(output_path, "PNG")
    print(f"Processed: {input_path} -> {output_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Texture Post-Processing Pipeline")
    parser.add_argument("input", help="Input image path")
    parser.add_argument("output", help="Output image path")
    parser.add_argument("--glass", action="store_true", help="Apply glass transparency")
    parser.add_argument("--keep-bg", action="store_true", help="Keep background (for block faces)")
    
    args = parser.parse_args()
    process_texture(args.input, args.output, args.glass, args.keep_bg)
