from PIL import Image
import os

# Get a list of all .ppm files in the current directory
ppm_files = [f for f in os.listdir('.') if f.endswith('.ppm')]

# Loop through the list of files
for filename in ppm_files:
    # Open the .ppm file
    with Image.open(filename) as img:
        # Convert the file name to .jpeg
        jpeg_filename = f"{os.path.splitext(filename)[0]}.jpeg"
        # Save the image as a JPEG
        img.save(jpeg_filename, "JPEG")

print("Conversion complete.")

