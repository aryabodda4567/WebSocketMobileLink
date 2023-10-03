import qrcode
from PIL import Image, ImageTk
import tkinter as tk
import util

def generate_qr_code(url):
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(url)
    qr.make(fit=True)

    qr_image = qr.make_image(fill_color="black", back_color="white")
    return qr_image

def close_window():
    root.destroy()

def main():
    # Create a tkinter window
    global root
    root = tk.Tk()
    root.title("QR Code Generator")

    # Generate the QR code
    url = f"ws://{util.util.get_ipaddress()}:51502"  # Replace with your desired URL
    qr_image = generate_qr_code(url)
    qr_image = qr_image.resize((300, 300), Image.Resampling.BILINEAR)  # Use Image.ANTIALIAS here

    tk_qr_image = ImageTk.PhotoImage(qr_image)

    # Create a label to display the QR code
    qr_label = tk.Label(root, image=tk_qr_image)
    qr_label.pack()

    text_label = tk.Label(root, text="Scan this QR from the installed mobile application")
    text_label.pack()
    # Create a close button
    close_button = tk.Button(root, text="Close", command=close_window)
    close_button.pack()

    # Set a custom window size
    root.geometry("400x400")

    # Make the window movable
    root.overrideredirect(True)

    # Center the window on the screen
    root.eval('tk::PlaceWindow . center')

    root.mainloop()

 

if __name__ == "__main__":
    main()
