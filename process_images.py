import sys
from PIL import Image

def process_image(input_path, output_path, is_glass=False):
    img = Image.open(input_path).convert("RGBA")
    datas = img.getdata()
    new_data = []
    
    for item in datas:
        r, g, b, a = item
        
        # Check if pixel is "green screen"
        if g > 180 and r < 50 and b < 50:
            # Change to transparent
            new_data.append((0, 0, 0, 0))
        else:
            # If it's glass, let's make the bluish parts semi-transparent
            if is_glass and b > 180 and g > 150 and r < 200:
                # Semi-transparent glass
                new_data.append((r, g, b, 150))
            else:
                new_data.append((r, g, b, a))

    img.putdata(new_data)
    # Resize to 16x16 to get true Minecraft feel
    img = img.resize((16, 16), resample=Image.NEAREST)
    img.save(output_path, "PNG")

if __name__ == "__main__":
    process_image(sys.argv[1], sys.argv[2], sys.argv[3] == "True" if len(sys.argv) > 3 else False)
