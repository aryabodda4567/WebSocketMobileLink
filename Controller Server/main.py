import web_socket
from util import util
import asyncio
import websockets
import display_qr

class Main:
    def __init__(self) -> None:
        display_qr.main()
        self.server_address = util.get_ipaddress()  # Use server_address to listen on all available network interfaces
        self.server_port = 51502 #util.get_port_number()  # Choose a port for your WebSocket server
        start_server = websockets.serve( web_socket.handle_client, self.server_address, self.server_port)
        print(f"ws://{self.server_address}:{self.server_port}")
        asyncio.get_event_loop().run_until_complete(start_server)
        asyncio.get_event_loop().run_forever()

        
# start the server
obj = Main() 