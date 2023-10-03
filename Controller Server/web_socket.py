import asyncio
import websockets
import json
from util import util
import UI
import datetime
  
 

# Store connected clients in a list (limited to two clients)
connected_clients = []
flag=0

async def handle_client(websocket, path):
    # print(f"Client connected: {websocket.remote_address}")
    global flag 
    flag = flag+1
    try:
        if len(connected_clients) < 2:
            print(f"Started: {datetime.datetime.now().time()}")
            # Add the client to the connected clients list
            connected_clients.append(websocket)
            if(flag==1):
                 UI.diplay()
                 
        while True:
            # Receive JSON messages from the client
            message = await websocket.recv()
            data = json.loads(message)
            print(f"Received message from {websocket.remote_address}: {data}")

            if websocket == connected_clients[1]:
                # Send messages from the second client (whose IP can vary) to the first client (server's IP)
                await connected_clients[0].send(message)# first connecter is reciver
                print(f"Forwarded message to {connected_clients[0].remote_address}: {data}")

            if websocket==connected_clients[0]:
                #send message from first client 
                await connected_clients[1].send(message)
                # with open("data.json", 'w+') as json_file:
                    # json.dump(data, json_file)
                print(f"Forwarded message to {connected_clients[1].remote_address}: {data}")


    except websockets.exceptions.ConnectionClosed:
        # Remove the client from the list if the connection is closed
        connected_clients.remove(websocket)
        print(f"Client disconnected: {websocket.remote_address}  {datetime.datetime.now().time()} ")


    

if __name__ == "__main__":
    server_address = util.get_ipaddress()  # Use server_address to listen on all available network interfaces
    server_port = 51502 #util.get_port_number()  # Choose a port for your WebSocket server

    start_server = websockets.serve(handle_client, server_address, server_port)
    print(f"ws://{server_address}:{server_port}")

    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()


