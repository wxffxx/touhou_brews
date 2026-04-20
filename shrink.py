import sys
from PIL import Image

def shrink_image(img_path):
    img = Image.open(img_path).convert("RGBA")
    # Resize to 16x16
    img = img.resize((16, 16), resample=Image.NEAREST)
    img.save(img_path, "PNG")

if __name__ == "__main__":
    for i in range(1, len(sys.argv)):
        shrink_image(sys.argv[i])
